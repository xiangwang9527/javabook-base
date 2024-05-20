package cn.javabook.chapter02.annotate.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拼装注解，形成完整的字段嵌套注解
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn {
	ColumnType columntype() default @ColumnType;
	ExtraInfo extrainfo() default @ExtraInfo;
    Constraints constraints() default @Constraints;
}
