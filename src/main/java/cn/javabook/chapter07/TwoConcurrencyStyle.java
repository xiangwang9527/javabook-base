package cn.javabook.chapter07;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 展示两种不同的并发风格
 *
 */
public class TwoConcurrencyStyle {
    public static void main(String[] args) throws InterruptedException {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Hello Virtual Thread");
//                System.out.println(Thread.currentThread().getName());
//            }
//        };
//
//        Thread thread1 = Thread.ofPlatform().daemon().start(runnable);
//        Thread thread2 = Thread.ofPlatform().name("duke").unstarted(runnable);
//        thread2.start();
//        // 创建平台线程：增加前缀并且指定标志位
//        ThreadFactory factory1 = Thread.ofPlatform().daemon().name("worker-", 1).factory();
//        Thread thread3 = factory1.newThread(runnable);
//        thread3.start();
//
//        /*
//         * 从thread4至thread7，它们执行方式完全是异步的
//         */
//        // 创建一个虚拟线程
//        Thread thread4 = Thread.ofVirtual().unstarted(() -> {
//            System.out.println("Hello Virtual Thread4");
//            // 因为没有提前设置，所以下面一行代码执行后显示不出线程名，虚拟线程没有平台线程那样的默认命名方式，
//            // 例如Thread-0、Thread-1、......
//            System.out.println(Thread.currentThread().getName());
//        });
//        thread4.start();
//        // 先命名虚拟线程再运行
//        Thread thread5 = Thread.ofVirtual().name("thread5").unstarted(runnable);
//        thread5.start();
//        // 通过虚拟线程工厂创建虚拟线程
//        ThreadFactory factory2 = Thread.ofVirtual().factory();
//        Thread thread6 = factory2.newThread(runnable);
//        thread6.setName("Thread6");
//        thread6.start();
//        // 通过虚拟线程工厂创建虚拟线程
//        ThreadFactory factory3 = Thread.ofVirtual().factory();
//        factory3.newThread(() -> {
//            System.out.println("Thread7");
//        }).start();
//        // 通过startVirtualThread()方法创建虚拟线程，它与Thread.ofVirtual().start(runnable)等价
//        Thread thread8 = Thread.startVirtualThread(runnable);
//        thread8.setName("thread8");
//        ThreadFactory factory = Thread.ofVirtual().factory();
//        ExecutorService service = Executors.newThreadPerTaskExecutor(factory);
//
//        Thread thread = Thread.ofVirtual().name("thread").unstarted(runnable);
//        thread.start();
//        // thread.stop();
//
//        TimeUnit.MILLISECONDS.sleep(1000);
    }
}
