package cn.javabook.chapter02.proxy;

/**
 * 代理的代理
 *
 */
public class NewProxy {
    private RentingProxy target;

    public NewProxy(RentingProxy target) {
        this.target = target;
    }

    public void zizhi() {
        System.out.println("展示资质");
        target.renthouse();
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        RentingProxy rentingProxy = new RentingProxy(consumer);
        NewProxy proxy = new NewProxy(rentingProxy);
        proxy.zizhi();
    }
}
