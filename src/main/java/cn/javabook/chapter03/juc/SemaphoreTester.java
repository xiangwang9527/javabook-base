package cn.javabook.chapter03.juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore"信号量"或"摇号机"
 *
 */
public class SemaphoreTester implements Runnable {
	static final Semaphore semaphore = new Semaphore(3);

	@Override
	public void run() {
		try {
			semaphore.acquire();
			System.out.println(Thread.currentThread().getName() + " 开始进餐");
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		semaphore.release();
	}

	public static void main(String[] args) {
		ExecutorService excutor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			excutor.submit(new SemaphoreTester());
		}
		excutor.shutdown();
	}
}
