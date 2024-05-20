package cn.javabook.chapter03.threadapi;

import java.util.concurrent.TimeUnit;

/**
 * 谦让（yield）
 *
 */
public class YieldThread {
    public static void main(String[] args) {
        // 创建t1线程
        Thread t1 = new Thread(() -> {
            System.out.println("t1 开始执行");
            // 调用yield()方法
            Thread.yield();
            System.out.println("t1 执行结束");
        });

        // 创建t2线程
        Thread t2 = new Thread(() -> {
            System.out.println("t2 开始执行");
            try {
                // 休眠1秒
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("t2 执行结束");
        });

        // 启动线程
        t1.start();
        t2.start();
    }
}
