package cn.javabook.chapter07;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * 某市大剧院可容纳5000人，但由于剧院目前在装修扩建，所以现在每场只能允许1000名观众入场
 * 如果表演方要售出1万张门票，那么原计划他们需要演2场，但现在需要演10场才能把票卖完
 *
 */
public class GrandTheatre {
    // 每次限进1000名观众
    static final Semaphore semaphore = new Semaphore(1000);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // 剧院可容纳5000人
        ExecutorService service = Executors.newFixedThreadPool(5000);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        start = System.currentTimeMillis();
        // 准备1万张票，准备表演10场
        IntStream.range(0, 10000).forEach(i -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 检票，只能进1000人
                    semaphore.acquire();
                    // 该批1000观众正在欣赏表演......，持续时间......1秒......
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                // 散场，准备下一场
                semaphore.release();
            // 这里如果不指定自定义的线程池，则会使用ForkJoinPool.commonPool，但结果可能无法预料
            }, service);
            futures.add(future);
        });
        // 表演场次已经安排好，一场接一场，直到10000张票全部卖完
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        service.shutdown();
        System.out.println("总共出演：" + (System.currentTimeMillis() - start) / 1000 + " 场");
    }
}
