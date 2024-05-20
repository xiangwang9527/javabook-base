package cn.javabook.chapter03.demo;

/**
 * 抽取出公共方法实现资源共享
 *
 */
public class ExtractMethodRunnable implements Runnable {
    private int i = 1;

    @Override
    public void run() {
        while (i <= 10) {
            System.out.println("当前线程: " + Thread.currentThread() + " - " + i++);
        }
    }

    public static void main(String[] args) {
        ExtractMethodRunnable extractMethod = new ExtractMethodRunnable();
        Thread thread1 = new Thread(extractMethod);
        Thread thread2 = new Thread(extractMethod);
        thread1.start();
        thread2.start();
    }
}
