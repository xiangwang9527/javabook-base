package cn.javabook.chapter03.cas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用AtomicInteger实现CAS
 *
 */
public class AtomicIntegerTest1 {
    /**
     * 以volatile关键字修饰变量
     */
    public static volatile AtomicInteger atomic = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        // 启动定长线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建10个线程
        for (int i = 0; i < 10; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    // 每个线程都执行累加操作
                    atomic.getAndIncrement();
                }
            };
            // 将线程提交到线程池
            executor.submit(runnable);
        }
        TimeUnit.MILLISECONDS.sleep(100);
        executor.shutdown();
        System.out.println(atomic.get());
    }
}
