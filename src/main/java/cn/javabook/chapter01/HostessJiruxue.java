package cn.javabook.chapter01;

import java.util.concurrent.TimeUnit;

/**
 * 洗衣服的回调接口，通知女主人衣服已经洗完
 * 这里用的是函数式接口
 *
 */
@FunctionalInterface
interface Wash {
    // 洗衣服
    public void finish();
}

/**
 * 女主人姬如雪
 *
 */
public class HostessJiruxue {
    public void washClothes(Wash wash) {
        // 姬如雪把衣服丢到洗衣机
        try {
            // 洗衣服中
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 衣服洗完了
        wash.finish();
    }

    public void dosomething() {
        System.out.println("逛街、遛娃 + 遛狗中......");
    }

    public static void main(String[] args) {
        System.out.println("把衣服放到洗衣机，先忙别的");
        // 准备把衣服丢到洗衣机
        new Thread(
                // 丢到洗衣机
                () -> new HostessJiruxue().washClothes(
                        // 洗衣服
                        () -> System.out.println("嘀～嘀～嘀～.......（衣服洗完了）")
                )
        ).start();
        // 姬如雪去忙别的了
        new HostessJiruxue().dosomething();
    }
}
