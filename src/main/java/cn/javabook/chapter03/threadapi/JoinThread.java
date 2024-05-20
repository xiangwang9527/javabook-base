package cn.javabook.chapter03.threadapi;

import java.util.concurrent.TimeUnit;

/**
 * 插队（join）
 *
 */
public class JoinThread {
    /**
     * 标志位
     *
     */
    public static String flag = "";

    public static void main(String[] args) {
        // 创建t1线程，其实它只是个龙套
        Thread t1 = new Thread(() -> {
            System.out.println("thread1");
        });

        // 创建t2线程
        Thread t2 = new Thread(() -> {
            try {
                System.out.println("thread2");
                // 通过设置不同的sleep()时间，观察flag是否被赋值
                TimeUnit.MILLISECONDS.sleep(100);
                flag = "join";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t2.start();
        try {
            // t2.join()：表示等待t2线程执行完毕，相当于join(0)
            // t2.join(n) ：表示最多等待t2线程执行n毫秒之后再处理
            // 如果join时间 < sleep时间，则sleep停止运行，运行时间就是join的时间。相当于方法被截断
            // 如果join时间 > sleep时间，则sleep继续运行，运行时间就是sleep的时间
            t2.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.start();
        System.out.println("flag = " + flag);
    }
}
