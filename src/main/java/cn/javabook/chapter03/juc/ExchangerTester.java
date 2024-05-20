package cn.javabook.chapter03.juc;

import java.util.concurrent.Exchanger;

/**
 * Exchanger"交换机"
 *
 */
public class ExchangerTester implements Runnable {
	Exchanger<Object> exchanger = null;
	Object object = null;

	public ExchangerTester(Exchanger<Object> exchanger, Object object) {
		this.exchanger = exchanger;
		this.object = object;
	}

	@Override
	public void run() {
		try {
			Object previous = this.object;
			this.object = this.exchanger.exchange(this.object);
			System.out.println(Thread.currentThread().getName() + " 用对象 " + previous + " 交换对象 " + this.object);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Exchanger<Object> exchanger = new Exchanger<Object>();
		new Thread(new ExchangerTester(exchanger, "A")).start();
		new Thread(new ExchangerTester(exchanger, "B")).start();
	}
}
