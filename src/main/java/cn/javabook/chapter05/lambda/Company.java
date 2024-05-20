package cn.javabook.chapter05.lambda;

import java.util.List;

/**
 * 公司类
 *
 */
public class Company<T> {
	public enum Type { BIG, SMALL };
	private String name;
	private Type type;
	private List<T> employees;

	public Company(final String name, final Type type, final List<T> employees) {
		super();
		this.name = name;
		this.type = type;
		this.employees = employees;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<T> getEmployees() {
		return employees;
	}

	public void setEmployees(List<T> employees) {
		this.employees = employees;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
