package cn.javabook.chapter03.demo;

/**
 * 继承Thread并重写run()接口
 *
 */
public class SubThread extends Thread {
    private int i = 1;

    @Override
    public void run() {
        while (i <= 10) {
            System.out.println("当前线程: " + Thread.currentThread() + " - " + i++);
        }
    }

    public static void main(String[] args) {
        SubThread subThread1 = new SubThread();
        SubThread subThread2 = new SubThread();
        subThread1.start();
        subThread2.start();
    }
}
