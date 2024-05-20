package cn.javabook.chapter03.cas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 使用AtomicIntegerFieldUpdater实现CAS
 *
 */
public class AtomicIntegerTest2 {
    /**
     * 使用AtomicIntegerFieldUpdater实现CAS，相关计算机段必须用volatile关键字修饰，不然抛异常
     * Caused by: java.lang.IllegalArgumentException: Must be volatile type
     */
    public volatile int count = 0;
    public static final AtomicIntegerFieldUpdater<AtomicIntegerTest2> lockUpdate = AtomicIntegerFieldUpdater.newUpdater(AtomicIntegerTest2.class, "count");

    public void increase(int inc) {
        lockUpdate.addAndGet(this, inc);
    }

    public int get() {
        return lockUpdate.get(this);
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerTest2 test = new AtomicIntegerTest2();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    test.increase(1);
                }
            };
            executor.submit(runnable);
        }
        TimeUnit.MILLISECONDS.sleep(100);
        executor.shutdown();
        System.out.println(test.get());
    }
}
