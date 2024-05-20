package cn.javabook.chapter03.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 厨师上菜
 *
 */
public class SichuanCuisine {
    public static BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

    /**
     * 一个往里放
     */
    static class Producer implements Runnable {
        @Override
        public void run() {
            try {
                queue.put("川菜");
                System.out.println("厨师做好" + Thread.currentThread().getName() +  "川菜");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 一个往外拿
     */
    static class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                String food = queue.take();
                System.out.println("客人消费" + Thread.currentThread().getName() + food);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        // 客人吃菜
        for (int i = 0; i < 5; i++) {
            new Thread(new Consumer(), "第 " + i + " 道").start();
        }
        // 厨师上菜
        for (int i = 0; i < 5; i++) {
            new Thread(new Producer(), "第 " + i + " 道").start();
        }
    }
}
