package cn.javabook.chapter03.threadapi;

import java.util.concurrent.TimeUnit;

/**
 * 等待（wait）与通知（notify/notifyAll）
 *
 */
public class WaitNotify {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println("t1 等待");
            synchronized ("锁") {
                System.out.println("t1 开始");
                try {
                    // TODO 执行线程任务
                    // t1进入等待队列并释放锁
                    "锁".wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1 结束");
            }
        });
        Thread t2 = new Thread(() -> {
            System.out.println("t2 等待");
            synchronized ("锁") {
                System.out.println("t2 开始");
                try {
                    // TODO 执行线程任务
                    // 通知其他所有线程（t1和t3）进入同步队列
                    // 这里如果用notify()，会发生什么？
                    "锁".notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("t2 结束");
            }
        });
        Thread t3 = new Thread(() -> {
            System.out.println("t3 等待");
            synchronized ("锁") {
                System.out.println("t3 开始");
                try {
                    // TODO 执行线程任务
                    TimeUnit.MILLISECONDS.sleep(1000);
                    // t3进入等待队列并释放锁
                    "锁".wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t3 结束");
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }
}
