package cn.javabook.chapter03.demo;

import java.util.concurrent.TimeUnit;

/**
 * 一边打游戏一边听音乐
 *
 */
public class GameAndMusic {
    private static void playGame() {
        for (;;) {
            System.out.println("打游戏");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void listenMusic() {
        for (;;) {
            System.out.println("听音乐");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        // 这里记得要把线程放在第一个执行，否则它永远也不会执行。因为自旋锁都是无限循环且不会继续往下执行的
        new Thread(GameAndMusic::playGame, "打游戏听音乐").start();
        listenMusic();
    }
}
