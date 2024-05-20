package cn.javabook.chapter03.demo;

import java.util.concurrent.TimeUnit;

/**
 * 多线程生命周期状态
 *
 */
public class MoreThreadState implements Runnable {
    private synchronized void sync(){
        try {
            TimeUnit.SECONDS.sleep(2);
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sync();
    }

    public static void main(String[] args) {
        MoreThreadState state = new MoreThreadState();
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
            // WAITING，因为sync()中的sleep()已经执行完，正在执行wait()方法
            System.out.println(thread1.getState());
            // TIMED_WAITING，因为thread1执行wait()方法时会释放锁，所以此时就由thread2来执行sync()方法，故此进入TIMED_WAITING
            System.out.println(thread2.getState());
            throw new RuntimeException();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
