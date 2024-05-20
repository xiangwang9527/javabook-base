package cn.javabook.chapter03.lockaqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 用AQS实现共享锁
 *
 */
public class AqsShareLockTest {
	public static void main(String[] args) throws InterruptedException {
		/**
		 * 一次允许发放三把锁
		 */
		AqsShareLock.count = 3;
		final Lock lock = new AqsShareLock();

		// 模拟10个客人就餐
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						lock.lock();
						System.out.println("持有 " + Thread.currentThread().getName() + " 的客人可以进餐厅就餐");
						// 每两次叫号之间间隔一段时间，模拟真实场景
						TimeUnit.MILLISECONDS.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						// 使用完成释放锁
						lock.unlock();
					}
				}
			}).start();
		}
	}
}
