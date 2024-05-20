package cn.javabook.chapter07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 工作窃取算法
 *
 */
public class WorkStealing {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger atomic = new AtomicInteger();
        long startTime = System.currentTimeMillis();
        ExecutorService normal = Executors.newFixedThreadPool(100);
        // 使用Executors.newWorkStealingPool()和new ForkJoinPool()效果一样
        // 它们的区别是：
        // 1. Executors.newWorkStealingPool()是在异步模式下创建的线程池，而new ForkJoinPool()则没有
        // 2. Executors.newWorkStealingPool()使用先进先出（FIFO）队列，而new ForkJoinPool()则用后进先出（LIFO）队列
        // ForkJoinPool forkjoin = new ForkJoinPool();
        ExecutorService forkjoin = Executors.newWorkStealingPool();
        IntStream.range(0, 1_000_0000).forEach(i -> normal.submit(atomic::incrementAndGet));
        normal.shutdown();
        System.out.println("定长线程池耗时：" + (System.currentTimeMillis() - startTime));

        startTime = System.currentTimeMillis();
        IntStream.range(0, 1_000_0000).forEach(i -> forkjoin.submit(atomic::incrementAndGet));
        forkjoin.shutdown();
        System.out.println("工作窃取耗时：" + (System.currentTimeMillis() - startTime));
    }
}
