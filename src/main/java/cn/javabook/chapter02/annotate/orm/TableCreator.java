package cn.javabook.chapter02.annotate.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解处理器
 *
 */
public class TableCreator {
	/**
	 * 解析类型注解
	 */
	private static String getColumnType(ColumnType columntype) {
		String type = "";
		switch (columntype.value()) {
			case CHAR:
				type += "CHAR";
				break;
			case STRING:
				type += "VARCHAR";
				break;
			case BOOLEAN:
				type += "BIT";
				break;
			case INTEGER:
				type += "INT";
				break;
			case LONG:
				type += "BIGINT";
				break;
			case FLOAT:
				type += "FLOAT";
				break;
			case DOUBLE:
				type += "DOUBLE";
				break;
			case DATETIME:
				type += "DATETIME";
				break;
			default:
				type += "VARCHAR";
				break;
		}

		return type;
	}

	/**
	 * 解析信息注解
	 */
	private static String getExtraInfo(ExtraInfo extrainfo) {
		String info = "";
		if (null != extrainfo.name()) {
			info = extrainfo.name();
		} else {
			return null;
		}
		if (0 < extrainfo.length()) {
			info += " (" + extrainfo.length() + ")";
		} else {
			return null;
		}

		return info;
	}

	/**
	 * 解析约束注解
	 */
	private static String getConstraints(Constraints con) {
		String constraints = "";
		if (con.primaryKey()) {
			constraints += " PRIMARY KEY";
		}
		if (!con.allowNull()) {
			constraints += " NOT NULL";
		}
		if (con.unique()) {
			constraints += " UNIQUE";
		}

		return constraints;
	}

	private static void createTable(List<String> list) {
		for (String className : list) {
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
				DBTable dbTable = clazz.getAnnotation(DBTable.class);
				if (dbTable == null) {// 无DBTable注解
					continue;
				}

				// 转大写
				String tableName = clazz.getSimpleName().toUpperCase();
				StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + "(");
				for (Field field : clazz.getDeclaredFields()) {
					// 通过反射得到注解
					Annotation[] anns = field.getDeclaredAnnotations();
					if (anns.length < 1) {
						continue;
					}
					String columnInfo = "";
					// 类型判断
					if (anns[0] instanceof TableColumn) {
						TableColumn column = (TableColumn) anns[0];
						String type = getColumnType(column.columntype());
						columnInfo = getExtraInfo(column.extrainfo());
						// 代替(
						columnInfo = columnInfo.replace("(", type + "(");
						columnInfo += getConstraints(column.constraints());
					}
					sql.append("\n " + columnInfo + ",");
				}
				// 删除尾部的逗号
				String tableCreate = sql.substring(0, sql.length() - 1) + "\n);";
				System.out.println(tableCreate);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Class<?> clazz = User.class;
		List<String> list = new ArrayList<>();
		list.add(clazz.getName());

		createTable(list);
	}
}
