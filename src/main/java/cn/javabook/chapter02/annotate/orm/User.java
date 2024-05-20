package cn.javabook.chapter02.annotate.orm;

/**
 * 用户实体类
 *
 */
@DBTable(name = "UserEntity")
public class User {
	@TableColumn(
			columntype = @ColumnType(Type.INTEGER), 
			extrainfo = @ExtraInfo(name = "id", length = 4),
			constraints = @Constraints(primaryKey = true))
	private String id;

	@TableColumn(
			columntype = @ColumnType(Type.STRING), 
			extrainfo = @ExtraInfo(name = "name", length = 32),
			constraints = @Constraints(primaryKey = false, allowNull = false, unique = true))
	private String name;

	@TableColumn(
			columntype = @ColumnType(Type.INTEGER), 
			extrainfo = @ExtraInfo(name = "age", length = 4),
			constraints = @Constraints(primaryKey = false))
	private Integer age;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public Integer getAge() { return age; }
	public void setAge(Integer age) { this.age = age; }

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}
