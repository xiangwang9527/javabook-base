package cn.javabook.chapter03.juc;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CyclicBarrier"栅栏"或"接力赛"
 *
 */
public class CyclicBarrierTester implements Runnable {
	private final static CyclicBarrier barrier = new CyclicBarrier(3);

	@Override
	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " 已接棒，等待结果...");
			// 只有最后一个线程执行后，所有的线程才能执行 2 所代表的动作
			barrier.await();
			TimeUnit.MILLISECONDS.sleep(1000);
			// 2 所有线程都会执行的动作
			System.out.println(Thread.currentThread().getName() + " 已成功拿下第一");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 3; i++) {
			executor.submit(new CyclicBarrierTester());
		}
		// 关闭线程池
		executor.shutdown();
	}
}
