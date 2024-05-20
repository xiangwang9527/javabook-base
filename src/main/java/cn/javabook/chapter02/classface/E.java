package cn.javabook.chapter02.classface;

/**
 * 类E同时实现A、B，会调用A、B哪个接口的方法呢？
 *
 */
public class E implements A, B {
    public static void main(String[] args) {
        new E().hello();
    }
}
