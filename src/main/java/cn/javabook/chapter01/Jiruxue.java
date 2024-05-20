package cn.javabook.chapter01;

import java.util.concurrent.TimeUnit;

/**
 * 做家务的姬如雪
 *
 */
public class Jiruxue {
    public void washClothes() {
        try {
            // 洗衣服
            System.out.println("手洗衣服1个小时");
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 衣服洗完了
        System.out.println("衣服洗完了");
    }

    public void dosomething() {
        System.out.println("逛街、买菜、遛娃 + 遛狗中......");
    }

    public static void main(String[] args) {
        System.out.println("开始洗衣服");
        Jiruxue jiruxue = new Jiruxue();
        jiruxue.washClothes();
        // 去忙别的了
        jiruxue.dosomething();
    }
}
