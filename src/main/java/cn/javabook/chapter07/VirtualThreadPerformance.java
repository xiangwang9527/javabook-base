package cn.javabook.chapter07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 虚拟线程：分别比较1_0000，10_0000，100_0000和1000_0000不同数量级下线程和协程的执行效率
 *
 */
public class VirtualThreadPerformance {
    public static void main(String[] args) {
//        AtomicInteger atomic = new AtomicInteger();
//        long startTime = System.currentTimeMillis();
//        // 由于在JDK 21中ExecutorService继承了AutoCloseable接口，
//        // 所以可以使用新的try-with-resources语法使Executor在最后能够被自动地关闭掉，
//        // 所以无需显式地执行shutdown()方法
//        ExecutorService service1 = Executors.newVirtualThreadPerTaskExecutor();
//        try {
//            IntStream.range(0, 1_0000_0000).forEach(i -> {
//                service1.submit(atomic::incrementAndGet);
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        long endTime = System.currentTimeMillis();
//        System.out.println("虚拟线程耗时：" + (endTime - startTime));
//
//        startTime = System.currentTimeMillis();
//        ExecutorService service2 = Executors.newCachedThreadPool();
//        try {
//            IntStream.range(0, 1_0000_0000).forEach(i -> {
//                service2.submit(atomic::incrementAndGet);
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        service2.shutdown();
//        endTime = System.currentTimeMillis();
//        System.out.println("平台线程耗时：" + (endTime - startTime));
    }
}
