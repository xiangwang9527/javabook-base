package cn.javabook.chapter06;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import reactor.core.publisher.Mono;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Mono实例
 *
 */
public class MonoDemo {
    private static final RedisURI redisUri = RedisURI.builder()
                                                     .withHost("172.16.185.166")
                                                     .withPort(6379)
                                                     .withDatabase(0)
                                                     .withPassword(new char[] {'1', '2', '3', '4', '5', '6'})
                                                     .build();
    private static final RedisClient redisClient = RedisClient.create(redisUri);
    private static final StatefulRedisConnection<String, String> conn = redisClient.connect();

    /*
     * 用命令式编程范式同步读取redis数据
     */
    public static void getMonoBySync() {
        System.out.println(conn.sync().get("cart"));
    }

    /*
     * 介于传统与反应式之间的异步方式读取redis数据
     */
    public static Mono<String> getMonoByAsync() {
        return Mono.create(sink -> {
            conn.async().get("cart").thenAccept(sink::success);
        });
    }

    /*
     * 用反应式编程范式读取redis数据
     */
    public static Mono<String> getMonoByReactive() {
        return conn.reactive().get("cart");
    }

    public static void main(String[] args) {
        Executor pool = Executors.newFixedThreadPool(10);

        // 同步操作耗时
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            pool.execute(MonoDemo::getMonoBySync);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("同步耗时：" + (endTime - startTime));

        // 异步操作耗时
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            pool.execute(() -> {
                getMonoByAsync().subscribe(System.out::println);
            });
        }
        endTime = System.currentTimeMillis();
        System.out.println("异步耗时：" + (endTime - startTime));

        // 反应式操作耗时
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            pool.execute(() -> {
                getMonoByReactive().subscribe(System.out::println);
            });
        }
        endTime = System.currentTimeMillis();
        System.out.println("反应式耗时：" + (endTime - startTime));

        // 主线程要等待子线程执行完成
        for (;;);
    }
}
