package cn.javabook.chapter02.classface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 隐式接口与显式接口
 *
 */
public class InterfaceType {
    public interface List {
        void get();
    }

    public static abstract class AbstractList implements List {
        public abstract void get();
    }

    /**
     * Class1显式实现接口
     */
    public static class Class1 extends AbstractList implements List {
        @Override
        public void get() {
            System.out.println("Class1.get()");
        }
    }

    /**
     * Class2隐式实现接口
     */
    public static class Class2 extends AbstractList {
        @Override
        public void get() {
            System.out.println("Class2.get()");
        }
    }

    /**
     * 用反射执行get()方法
     */
    private static <T> T invoke(final T obj) {
        final InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(obj, args);
            }
        };
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), handler);
    }

    public static void main(String[] args) {
        // 都可以调用成功
        Class1 class1 = new Class1();
        class1.get();
        Class2 class2 = new Class2();
        class2.get();

        // Class1可以调用成功
        List list1 = invoke(new Class1());
        list1.get();
        // Class2会抛异常。这是因为Class1显示实现接口List，而Class2隐式实现接口List
        List list2 = invoke(new Class2());
        list2.get();
    }
}
