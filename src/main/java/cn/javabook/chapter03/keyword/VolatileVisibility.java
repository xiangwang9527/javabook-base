package cn.javabook.chapter03.keyword;

import java.util.concurrent.TimeUnit;

/**
 * 可见性测试
 *
 */
public class VolatileVisibility {
    // 变量初始值
    static int init = 0;

    public static void main(String[] args) {
        // 启动Reader线程读取init
        new Thread(() -> {
            int local = init;
            while (local < 5) {
                if (init != local) {
                    System.out.println("Reader线程读取init为：" + init);
                    local = init;
                }
            }
        }, "Reader").start();
        // 启动Writer线程修改init
        new Thread(() -> {
            int local = init;
            while (local < 5) {
                // 修改init
                System.out.println("Writer线程将init赋值为：" + ++local);
                init = local;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Writer").start();
    }
}
