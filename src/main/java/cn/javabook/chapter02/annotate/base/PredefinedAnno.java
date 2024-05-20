package cn.javabook.chapter02.annotate.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义注解可以应用在哪里
 * PACKAGE：包
 * TYPE：类、接口（包括注解类型）或者enum
 * CONSTRUCTOR：构造器
 * METHOD：方法
 * FIELD：字段（包括enum实例）
 * LOCAL_VARIABLE：局部变量
 * PARAMETER：参数
 * 
 * 如果省去@Target注解，那么注解可以应用于所有的ElementType
 *
 */
@Target(ElementType.METHOD)
/**
 * 定义了注解在哪里可用
 * SOURCE：源代码，将被编译器丢弃
 * CLASS：class文件，会被JVM丢弃
 * RUNTIME：运行时，一直保留
 */
@Retention(RetentionPolicy.RUNTIME)
// 将此注解保存在Javadoc中
@Documented
// 允许子类继承父类的注解
@Inherited
// 允许一个注解可以被使用一次或者多次
// @Repeatable(value = Object.class)
// 标记注解，不包含任何元素
public @interface PredefinedAnno {}
