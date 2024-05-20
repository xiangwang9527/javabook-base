package cn.javabook.chapter03.keyword;

import java.util.concurrent.TimeUnit;

/**
 * synchronized关键字
 *
 */
public class SynchronizedThread implements Runnable {
    private static int tickets = 10;

    @Override
    public void run() {
        // 增加synchronized锁
        synchronized ("锁") {
            while (true) {
                if(tickets > 0) {
                    // 因为这里有--操作，所以给tickets变量加上volatile关键字修饰是无用的
                    System.out.println(Thread.currentThread().getName() + " 正在出售 " + (--tickets) + " 号车票");
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " 车票已售完");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        SynchronizedThread window = new SynchronizedThread();
        Thread thread1 = new Thread(window, "一号窗口");
        Thread thread2 = new Thread(window, "二号窗口");
        Thread thread3 = new Thread(window, "三号窗口");
        thread1.start();
        thread2.start();
        thread3.start();
    }
}
