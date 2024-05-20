package cn.javabook.chapter02.classface;

/**
 * 接口A
 *
 */
public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}
