package cn.javabook.chapter02.classface;

/**
 * 类F继承D，又同时实现A、B，会调用A、B、D哪个的方法呢？
 *
 */
public class F extends D implements A, B {
    public static void main(String[] args) {
        new F().hello();
    }
}
