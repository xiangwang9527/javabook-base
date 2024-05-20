package cn.javabook.chapter03.threadapi;

import java.util.concurrent.TimeUnit;

/**
 * 休眠（sleep）与打断（interrupt）
 *
 */
public class SleepInterrupt {
    public static void main(String[] args) {
        /*
         * 第一种情况：打断sleep
         */
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + " - " + i);
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
         * 第二种情况：打断wait
         */
        Thread t2 = new Thread(() -> {
            synchronized ("锁") {
                try {
                    "锁".wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
         * 第三种情况：正常打断
         */
        Thread t3 = new Thread(() -> {
            int i = 0;
            for (;;) {
                i = 1;
            }
        });

        // 使用sleep()时interrupt()
        t1.start();
        try {
            TimeUnit.MILLISECONDS.sleep(200);
            t1.interrupt();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("t1线程状态：" + t1.getState());
        System.out.println("t1打断状态：" + t1.isInterrupted());

        // 使用wait()时interrupt()
        t2.start();
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
            t2.interrupt();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("t2线程状态：" + t2.getState());
        System.out.println("t2打断状态：" + t2.isInterrupted());

        // 正常运行时interrupt()
        t3.start();
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
            t3.interrupt();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("t3线程状态：" + t3.getState());
        System.out.println("t3打断状态：" + t3.isInterrupted());
    }
}
