package cn.javabook.chapter03.demo;

import java.util.concurrent.TimeUnit;

/**
 * 多线程生命周期状态
 *
 */
public class StandardThreadState implements Runnable {
    private synchronized void sync(){
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sync();
    }

    public static void main(String[] args) {
        StandardThreadState state = new StandardThreadState();
        Thread thread1 = new Thread(state);
        Thread thread2 = new Thread(state);

        // NEW
        System.out.println(thread1.getState());
        thread1.start();
        // RUNNABLE
        System.out.println(thread1.getState());
        thread2.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            // TIMED_WAITING，因为正在执行sleep()方法
            System.out.println(thread1.getState());
            // BLOCKED，因为thread2没拿到sync()的锁
            System.out.println(thread2.getState());
            TimeUnit.MILLISECONDS.sleep(2000);
            // TERMINATED，因为thread1已经执行完
            System.out.println(thread1.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
