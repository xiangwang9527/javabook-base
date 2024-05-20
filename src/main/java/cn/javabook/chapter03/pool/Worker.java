package cn.javabook.chapter03.pool;

import java.util.concurrent.TimeUnit;

/**
 * 工人
 *
 */
public class Worker {
    public void dosomething() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("挖坑");
    }
}
