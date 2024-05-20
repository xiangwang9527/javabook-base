package cn.javabook.chapter03.lockaqs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 悲观锁与乐观锁的调用方式
 *
 */
public class PessimisticAndOptimisticLock {
    /**
     * 悲观锁的调用方式
     */
    // 方式一：使用synchronized关键字
    public synchronized void callPessimisticLock() {
        // TODO 操作共享资源
    }
    // 方式二：使用可重入锁
    private ReentrantLock lock = new ReentrantLock();
    public void operateResource() {
        // 加锁
        lock.lock();
        // TODO 操作共享资源
        // 释放锁
        lock.unlock();
    }

    /**
     * 乐观锁的调用方式
     */
    private AtomicInteger atomic = new AtomicInteger();
    public void optimistic() {
        // 执行原子操作
        atomic.getAndIncrement();
    }

    public static void main(String[] args) {
        for (;;) {
            // TODO
        }
    }
}
