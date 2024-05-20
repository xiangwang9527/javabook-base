package cn.javabook.chapter03.demo;

import java.util.concurrent.TimeUnit;

/**
 * 使用return停止线程
 *
 */
public class StopThreadC extends Thread {
    @Override
    public void run() {
        while (true) {
            if (this.isInterrupted()) {
                System.out.println("已停止");
                return;
            }
            System.out.println(System.currentTimeMillis());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StopThreadC threadC = new StopThreadC();
        threadC.start();
        TimeUnit.MILLISECONDS.sleep(100);
        threadC.interrupt();
    }
}
