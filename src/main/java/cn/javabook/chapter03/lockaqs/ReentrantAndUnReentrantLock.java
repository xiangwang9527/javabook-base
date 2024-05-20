package cn.javabook.chapter03.lockaqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 可重入锁与不可重入锁
 *
 */
public class ReentrantAndUnReentrantLock implements Lock {
    private Thread thread;

    public synchronized void outerMethod() {
        System.out.println("外层方法执行");
        // 执行内层方法时自动获得锁
        innerMethod();
    }

    public synchronized void innerMethod() {
        System.out.println("内层方法执行");
    }

    @Override
    public void lock() {
        synchronized (this) {
            // 已线程持有锁时
            while (thread != null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 绑定当前线程
            this.thread = Thread.currentThread();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        synchronized (this) {
            // 持有锁的线程不是当前线程时
            if (Thread.currentThread() != thread) {
                return;
            }
            // 解绑线程并唤醒所有其他线程
            this.thread = null;
            notifyAll();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    public static void main(String[] args) {
        ReentrantAndUnReentrantLock lock = new ReentrantAndUnReentrantLock();

        // 可重入锁
        lock.outerMethod();

        // 不可重入锁
        lock.lock();
        try {
            System.out.println("外层锁");
            lock.lock();
            try {
                System.out.println("内层锁");
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }
}
