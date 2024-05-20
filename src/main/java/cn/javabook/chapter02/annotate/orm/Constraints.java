package cn.javabook.chapter02.annotate.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 明确字段约束
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraints {
	boolean primaryKey() default false;
    boolean allowNull() default true;
    boolean unique() default false;
    // 还可以增加默认值
}
