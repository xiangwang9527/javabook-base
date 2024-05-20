package cn.javabook.chapter02.proxy;

/**
 * 代理实现
 *
 */
public class RentingProxy implements Renting {
    private Renting renting;

    public RentingProxy(Renting renting) {
        this.renting = renting;
    }

    @Override
    public void renthouse() {
        System.out.println("代理方法前置：租房前看房");
        renting.renthouse();
        System.out.println("代理方法后置：满意签合同");
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.renthouse();
        System.out.println("===================");
        Renting proxy = new RentingProxy(consumer);
        proxy.renthouse();
    }
}
