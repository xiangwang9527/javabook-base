package cn.javabook.chapter02.classface;

/**
 * 接口B
 *
 */
public interface B extends A {
    default void hello() {
        System.out.println("Hello from B");
    }
}
