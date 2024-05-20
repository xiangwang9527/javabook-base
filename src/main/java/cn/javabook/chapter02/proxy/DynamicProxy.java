package cn.javabook.chapter02.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理实现
 *
 */
public class DynamicProxy {
    // JDK动态代理实现
    private static Object getProxyObject(final Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                // 实现接口InvocationHandler
                target.getClass().getInterfaces(), new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 这里可以实现前置方法
                        System.out.println("代理方法前置1：租房前看中介资质");
                        System.out.println("代理方法前置2：租房前看房");
                        if (null != args) {
                            for (Object arg : args) {
                                System.out.println(" - " + arg);
                            }
                        }
                        // 调用接口方法
                        Object result = method.invoke(target, args);
                        // 这里可以实现后置方法
                        System.out.println("代理方法后置：满意签合同");
                        return result;
                    }
                });
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.renthouse();
        System.out.println("===================");
        Renting renting = (Renting) getProxyObject(consumer);
        renting.renthouse();
    }
}
