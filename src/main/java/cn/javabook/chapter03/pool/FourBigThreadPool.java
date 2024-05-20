package cn.javabook.chapter03.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * "四大"线程池
 *
 */
public class FourBigThreadPool {
    public static void main(String[] args) {
        // 定长线程池
        ExecutorService service1 = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            System.out.println("创建线程 " + i);
            service1.execute(() -> System.out.println("当前线程 " + Thread.currentThread().getName()));
        }
        service1.shutdown();

        // 缓存线程池
        ExecutorService service2 = Executors.newCachedThreadPool();
        for (int i = 0; i < 1_000_000_000; i++) {
            System.out.println("创建线程 " + i);
            service2.execute(() -> System.out.println("当前线程 " + Thread.currentThread().getName()));
        }
        service2.shutdown();

        // 单线程线程池
        ExecutorService service3 = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            System.out.println("创建线程 " + i);
            service3.execute(() -> System.out.println("当前线程 " + Thread.currentThread().getName()));
        }
        service3.shutdown();

        // 任务调度线程池
        ScheduledExecutorService service4 = Executors.newScheduledThreadPool(3);
        for (int i = 0; i < 10; i++) {
            // 延迟1秒后执行，仅执行1次
            service4.schedule(() -> System.out.println("当前线程 " + Thread.currentThread().getName()),
                    1,
                    TimeUnit.SECONDS);
        }
        service4.shutdown();

        // 任务调度线程池
        ScheduledExecutorService service5 = Executors.newScheduledThreadPool(3);
        for (int i = 0; i < 10; i++) {
            // 延迟1秒后执行，每3秒执行1次
            service4.scheduleAtFixedRate(() -> System.out.println("当前线程 " + Thread.currentThread().getName()),
                    1,
                    3,
                    TimeUnit.SECONDS);
        }
        service5.shutdown();
    }
}
