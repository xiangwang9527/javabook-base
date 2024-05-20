package cn.javabook.chapter05.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OldMethod {
	public static List<Employee> filter(List<Employee> employees, EmployeeInterface<Employee> ei) {
		List<Employee> list = new ArrayList<>();
		for(Employee employee : employees) {
			if (ei.select(employee)) {
				list.add(employee);
			}
		}
		return list;
	}

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
		 * 用传统方法实现大厂员工联谊登记
		 */
		List<Employee> unMarriedEmployee = new ArrayList<>();
		for (Company<Employee> company : list) {
			if (company.getType() == Company.Type.BIG) {
				List<Employee> emps = company.getEmployees();
				// 再次调用排序算法
				for (Employee tempEmployee : emps) {
					if (!tempEmployee.isMarried()) {
						unMarriedEmployee.add(tempEmployee);
					}
				}
			}
		}
		System.out.println("unMarriedEmployee -> " + unMarriedEmployee);

		/*
		 * 用传统方法实现员工年龄筛选并排序
		 */
		// 筛选28岁以下的员工
		List<Employee> list1 = new ArrayList<>();
		for(Employee employee : employees) {
			if (employee.getAge() < 28) {
				list1.add(employee);
			}
		}
		// 按年龄排序
		list1.sort(new Comparator<Employee>() {
			@Override
			public int compare(Employee o1, Employee o2) {
				return o1.getAge().compareTo(o2.getAge());
			}
		});

		/*
		 * 用lambda方式实现
		 */
		List<Employee> list2 = filter(employees, employee -> employee.getAge() <= 28);

		/*
		 * 流式计算方式
		 */
		List<Employee> list3 = employees
										// 生成"流"
										.stream()
										// 条件过滤
										.filter(emp -> emp.getAge() < 28)
										// 按年龄排序
										.sorted((o1, o2) -> o1.getAge().compareTo(o2.getAge()))
										// 生成新的结果集合
										.collect(Collectors.toList());
	}
}
