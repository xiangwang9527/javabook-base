package cn.javabook.chapter03.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch"发令枪"
 *
 */
public class CountDownLatchTester implements Runnable {
	static final CountDownLatch latch = new CountDownLatch(10);
	@Override
	public void run() {
		// 检查任务
		try {
			System.out.println(Thread.currentThread().getName() + " 检查完毕！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		for (int i = 10; i > 0; i--) {
			Thread.sleep(1000);
			executor.submit(new CountDownLatchTester());
			System.out.println(i);
		}

		TimeUnit.MILLISECONDS.sleep(1000);
		// 最后检查确认
		latch.await();
		System.out.println();
		System.out.println("点火，发射！");
		// 关闭线程池
		executor.shutdown();
	}
}
