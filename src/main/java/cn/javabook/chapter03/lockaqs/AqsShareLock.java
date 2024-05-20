package cn.javabook.chapter03.lockaqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 利用AQS实现自定义共享锁
 *
 */
public class AqsShareLock implements Lock {
	public static int count;
	private final SyncHelper synchepler = new SyncHelper(count);

	@Override
	public void lock() {
		synchepler.acquireShared(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		synchepler.acquireSharedInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		return synchepler.tryAcquireShared(1) > 0;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return synchepler.tryAcquireSharedNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		synchepler.releaseShared(1);
	}

	@Override
	public Condition newCondition() {
		return synchepler.newCondition();
	}

	/**
	 * 以内部类继承AQS的方式实现共享锁
	 *
	 */
	private static class SyncHelper extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = -7357716912664213942L;

		/**
		 * count表示允许几个线程能同时获得锁
		 */
		public SyncHelper(int count) {
			if (count <= 0) {
				throw new IllegalArgumentException("锁资源数量必须大于0");
			}
			// 设置资源总数
			setState(count);
		}

		/**
		 * 一次允许多少个线程进来，允许数量的线程都能拿到锁，其他的线程进入队列
		 */
		@Override
		protected int tryAcquireShared(int acquires) {
			// 自旋
			for (;;) {
				int state = getState();
				int remain = state - acquires;
				// 判断剩余锁资源是否已小于0或者CAS执行是否成功
				if (remain < 0 || compareAndSetState(state, remain)) {
					return remain;
				}
			}
		}

		/**
		 * 锁资源的获取和释放要一一对应
		 */
		@Override
		protected boolean tryReleaseShared(int releases) {
			// 自旋
			for (;;) {
				// 获取当前state
				int current = getState();
				// 释放状态state增加releases
				int next = current + releases;
				if (next < current) {// 溢出
					throw new Error("Maximum permit count exceeded");
				}
				// 通过CAS更新state的值
				// 这里不能用setState()
				if (compareAndSetState(current, next)) {
					return true;
				}
			}
		}

		protected Condition newCondition() {
			return new ConditionObject();
		}
	}
}
