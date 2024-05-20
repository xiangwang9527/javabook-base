package cn.javabook.chapter05.stream;

import cn.javabook.chapter05.lambda.Employee;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

public class StreamClient {
	public static void main(String[] args) {
		List<Employee> employees = Arrays.asList(
			new Employee("张三", "男", 30, true, Employee.Type.MANAGER),
			new Employee("李四", "男", 28, true, Employee.Type.SELLER),
			new Employee("王二", "男", 26, false, Employee.Type.SELLER),
			new Employee("赵六", "男", 29, false, Employee.Type.OFFICER),
			new Employee("钱七", "男", 32, true, Employee.Type.SELLER),
			new Employee("孙八", "男", 35, true, Employee.Type.OFFICER),
			new Employee("周九", "男", 22, false, Employee.Type.SELLER),
			new Employee("吴十", "男", 22, false, Employee.Type.OFFICER)
		);

		// 筛选与切片
		List<Integer> list1 = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
		list1.stream().filter(i -> i < 3)
					  .distinct()
					  .skip(1)
					  .forEach(System.out::println);

		// map：映射操作，把一种数据类型转换成另外一种数据类型
		List<String> list2 = employees.stream()
									  // 映射
									  .map(Employee::toString)
									  .collect(Collectors.toList());
		// flatMap：扁平化映射
		list2.add("hello world");
		list2.add("hello china");
		list2.stream().map(t -> t.split(" "))
					  // 扁平化映射。或element -> Arrays.stream(element)
					  .flatMap(Arrays::stream)
					  .distinct()
					  .forEach(System.out::println);

		// words = "i am chinese, i love china";

		// 查找与匹配
		// anyMatch、findAny、allMatch、noneMatch
		System.out.println(employees.stream()
				.anyMatch(emp -> emp.isMarried()));

		// 归约reduce
		List<Integer> list3 = Arrays.asList(9, 7, 1, 5, 8, 10, 6);
		Integer result = list3.stream()
				              // .reduce(1, (x, y) -> x + y);
							  .reduce(1, Integer::sum);

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*
		 * 下面是一些常见算子，放于此处供读者参考
		 */
		// 收集器、分组、分区、并行流
		// 流式编程
		Stream<Employee> stream = employees.stream()//.stream()
										// 过滤
										.filter(emp -> emp.getAge() < 30)
										// 排序
										.sorted((o1, o2) -> o1.getAge().compareTo(o2.getAge()));
		stream.forEach(System.out::println);
//										.forEach(System.out::println);
//										// 生成集合返回
//										.collect(Collectors.toList());

		long count1 = employees.stream().filter(Employee::isMarried).count();
		System.out.println(count1);

		List<Integer> list10 = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
		Integer result1 = list10.stream().reduce(1, (x, y) -> x + y);
		System.out.println(result1);

		// 有多少员工
		long count2 = employees.stream().collect(counting());
		System.out.println(count2);

		Optional<Employee> employee =
				employees.stream()
				.collect(maxBy((o1, o2) -> o1.getAge().compareTo(o2.getAge())));
		System.out.println(employee);

		int ages =
				employees.stream()
				.collect(summingInt(emp -> emp.getAge()));
		System.out.println(ages);

		// 归约
		IntSummaryStatistics statistics = employees.stream()
													.collect(Collectors.summarizingInt(Employee::getAge));
		String shortName = employees.stream()
									.map(Employee::getName)
									.collect(Collectors.joining(", "));
		System.out.println(shortName);

		String name = employees.stream()
								.map(Employee::getName)
								.collect(Collectors.reducing((s1, s2) -> s1 + s2))
								.get();
		System.out.println(name);

		int totalCalories = employees.stream().collect(reducing(0, Employee::getAge, (i, j) -> i + j));
		System.out.println(totalCalories);
		Optional<Employee> mostCalorieDish =
				employees.stream().collect(reducing(
				(d1, d2) -> d1.getAge() > d2.getAge() ? d1 : d2));
		System.out.println(mostCalorieDish);
		Employee emp = employees.stream().max((o1, o2) -> o1.getAge().compareTo(o2.getAge())).get();
		System.out.println(emp);

		// List转Map
		Map<Integer, Employee> map1 = employees
											.stream()
											.collect(toMap(Employee::getAge, 
															val -> (Employee) val, 
															(k, v) -> v, 
															HashMap::new)
											);
		System.out.println(map1);

		// 分组与多级分组
		// 按类型分组统计员工
		Map<Employee.Type, List<Employee>> map2 = employees.stream().collect(groupingBy(Employee::getType));
		System.out.println(map2);

		// 先按婚姻分组，再按类型分组
		Map<Boolean, Map<Employee.Type, List<Employee>>> map3 = employees.stream()
													.collect(groupingBy(Employee::isMarried, 
																		groupingBy(Employee::getType)));
		System.out.println(map3);

		// 先按婚姻分组，再按类型分组，并且以年龄来判断
		Map<Boolean, Map<Employee.Type, List<Employee>>> map4 = employees.stream()
											.collect(groupingBy(Employee::isMarried, groupingBy(emps -> {
			if (emps.getAge() < 22) {
				return Employee.Type.MANAGER;
			} else if(emps.getAge() < 24) {
				return Employee.Type.SELLER;
			} else {
				return Employee.Type.OFFICER;
			}
		})));
		System.out.println(map4);

		// 按子组收集数据
		// 按类型分组统计数量
		Map<Employee.Type, Long> map5 = employees.stream()
								.collect(groupingBy(Employee::getType, 
													counting()));
		System.out.println(map5);

		// 按类型分组统计年龄总和
		Map<Employee.Type, Integer> map6 = employees.stream()
								.collect(groupingBy(Employee::getType, 
													summingInt(Employee::getAge)));
		System.out.println(map6);

//		Map<Employee.Type, Set<CaloricLevel>> map =
//		employees.stream().collect(
//		groupingBy(Employee::getType, mapping(
//		emp -> {
//			if (emp.getAge() <= 22) return CaloricLevel.DIET;
//			else if (emp.getAge() <= 27) return CaloricLevel.NORMAL;
//			else return CaloricLevel.FAT;
//		},
//		toSet())));

		// 分区
		// 按婚姻分区
		Map<Boolean, List<Employee>> map7 = employees.stream()
										.collect(partitioningBy(Employee::isMarried));
		System.out.println(map7);

		// 分区后再分组
		Map<Boolean, Map<Employee.Type, List<Employee>>> map8 = employees.stream()
										.collect(partitioningBy(Employee::isMarried, 
													groupingBy(Employee::getType)));
		System.out.println(map8);

		// 分区后再分区
		Map<Boolean, Map<Boolean, List<Employee>>> map9 = employees.stream()
										.collect(partitioningBy(Employee::isMarried, 
												partitioningBy(emps -> emps.getAge() < 28)));
		System.out.println(map9);
	}
}
