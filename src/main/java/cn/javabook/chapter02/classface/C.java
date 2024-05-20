package cn.javabook.chapter02.classface;

/**
 * 接口C
 *
 */
public interface C {
    default void hello() {
        System.out.println("Hello from C");
    }
}
