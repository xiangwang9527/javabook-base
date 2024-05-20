package cn.javabook.chapter02.annotate.es;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * elastic字段注解，定义每个elasticsearch字段上的属性
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface DocField {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    FieldType type() default FieldType.Auto;

    boolean index() default false;

    String format() default "";

    String pattern() default "";

    boolean store() default false;

    boolean fielddata() default false;

    String searchAnalyzer() default "";

    String analyzer() default "";

    String normalizer() default "";
}
