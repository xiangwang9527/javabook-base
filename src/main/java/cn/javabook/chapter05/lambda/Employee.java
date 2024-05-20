package cn.javabook.chapter05.lambda;

/**
 * 员工类
 *
 */
public class Employee {
	public enum Type { MANAGER, SELLER, OFFICER };

	private String name;

	private String genger;

	private Integer age;

	private boolean married;

	private Type type;

	public Employee(final String name, final String genger, final Integer age, final boolean married, final Type type) {
		super();
		this.name = name;
		this.genger = genger;
		this.age = age;
		this.married = married;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGenger() {
		return genger;
	}

	public void setGenger(String genger) {
		this.genger = genger;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.name + "(" + this.genger + ")-" + this.age;
	}
}
