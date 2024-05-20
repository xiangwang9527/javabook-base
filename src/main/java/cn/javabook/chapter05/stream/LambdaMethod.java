package cn.javabook.chapter05.stream;

import cn.javabook.chapter05.lambda.Company;
import cn.javabook.chapter05.lambda.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LambdaMethod {
	public static void main(String[] args) {
		// 创造数据
		List<Company<Employee>> list = new ArrayList<>();
		List<Employee> employees = Arrays.asList(
			new Employee("张勇", "男", 28, true, Employee.Type.MANAGER),
			new Employee("李强", "男", 22, false, Employee.Type.SELLER),
			new Employee("王武", "男", 32, false, Employee.Type.SELLER),
			new Employee("梅丽", "女", 26, true, Employee.Type.OFFICER),
			new Employee("郑帅", "男", 29, false, Employee.Type.OFFICER),
			new Employee("曾美", "女", 27, true, Employee.Type.SELLER),
			new Employee("郝俊", "男", 22, true, Employee.Type.SELLER),
			new Employee("方圆", "女", 24, false, Employee.Type.SELLER)
		);
		Company<Employee> moubao = new Company<Employee>("某宝", Company.Type.BIG, employees);

		employees = Arrays.asList(
			new Employee("吴琼", "女", 27, true, Employee.Type.SELLER),
			new Employee("陈辰", "女", 20, false, Employee.Type.OFFICER),
			new Employee("刘能", "男", 25, true, Employee.Type.OFFICER),
			new Employee("周七", "男", 29, false, Employee.Type.OFFICER),
			new Employee("汪旺", "男", 21, false, Employee.Type.OFFICER),
			new Employee("胡涂", "男", 27, false, Employee.Type.OFFICER),
			new Employee("杨茂", "男", 34, true, Employee.Type.MANAGER),
			new Employee("朱坚", "男", 30, false, Employee.Type.MANAGER)
		);
		Company<Employee> mouxin = new Company<Employee>("某东", Company.Type.BIG, employees);

		employees = Arrays.asList(
			new Employee("冯过", "男", 35, false, Employee.Type.SELLER),
			new Employee("何花", "女", 27, false, Employee.Type.MANAGER),
			new Employee("卫精", "男", 25, true, Employee.Type.OFFICER),
			new Employee("施工", "男", 28, false, Employee.Type.OFFICER),
			new Employee("沈月", "女", 24, false, Employee.Type.OFFICER),
			new Employee("乐欢", "女", 22, false, Employee.Type.OFFICER),
			new Employee("安全", "男", 33, true, Employee.Type.MANAGER),
			new Employee("林森", "男", 26, true, Employee.Type.SELLER)
		);
		Company<Employee> wahaha = new Company<Employee>("某哈哈", Company.Type.SMALL, employees);
		// 加入列表
		list.add(moubao);
		list.add(mouxin);
		list.add(wahaha);

		/*
		 * 方式二：公司联谊未婚员工登记表：lambda表达式一
		 */
		List<Employee> unMarriedList2 = new ArrayList<>();
		// 第一次使用Lambda表达式
		List<Company<Employee>> result = list.stream().filter(company -> {
			if (company.getType() == Company.Type.BIG) {
//				// 没有排序的功能
//				company.getEmployees().stream().filter(employee -> {
//					if (!employee.isMarried()) {
//						unMarriedList2.add(employee);
//						return true;
//					} else {
//						return false;
//					}
//				}).collect(Collectors.toList());

				// 加上排序的功能
				company.getEmployees().stream().sorted(Comparator.comparing(Employee::getAge))
				.filter(employee -> {
					if (!employee.isMarried()) {
						unMarriedList2.add(employee);
						return true;
					} else {
						return false;
					}
				}).collect(Collectors.toList());
				return true;
			} else {
				return false;
			}
		}).collect(Collectors.toList());
		// 只能到公司纬度，完全不满足需求
		System.out.println("result -> " + result);
		// 用了Lambda，但按照公司分组来显示
		System.out.println("unMarriedList2 -> " + unMarriedList2);

		// 虽然上面使用了Lambda，但是并不完美，我们需要的是能够不分公司就按未婚和年龄排序，因此，尝试使用更完整的Lambda表达式
		/*
		 * 公司联谊未婚员工登记表：lambda表达式二
		 */
		List<Employee> unMarriedList3 = list.stream()
				// 过滤出公司规模
				.filter(company -> Company.Type.BIG == company.getType())
				// 类型映射，打破公司之间的隔离
				.flatMap(companys -> companys.getEmployees().stream())
				// 是否单身员工，使用方法引用
				.filter(Employee::isMarried)
				// 按年龄排序，使用方法引用
				.sorted(Comparator.comparing(Employee::getAge))
				// 集合转化
				.collect(Collectors.toList());
		// 完全能够满足需求
		System.out.println("unMarriedList3 -> " + unMarriedList3);

		/*
		 * 去掉回车换行之后，一行代码解决单身员工联谊问题：）
		 *
		 * 仅供参考，希望有更好的办法
		 */
		List<Employee> unMarriedList4 = list.stream().filter(company -> Company.Type.BIG == company.getType()).flatMap(companys -> companys.getEmployees().stream()).filter(Employee::isMarried).sorted(Comparator.comparing(Employee::getAge)).collect(Collectors.toList());
		System.out.println("unMarriedList4 -> " + unMarriedList4);
	}
}
