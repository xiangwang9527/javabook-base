package cn.javabook.chapter03.demo;

import java.util.concurrent.TimeUnit;

/**
 * 停止线程
 *
 */
public class StopService {
    private String username = "username";
    private String password = "password";

    public synchronized String getUsername() {
        return username;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized void printString(String username, String password) {
        try {
            this.username = username;
            TimeUnit.SECONDS.sleep(100);
            this.password = password;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StopService service = new StopService();
        StopThreadA threadA = new StopThreadA(service);
        threadA.start();
        StopThreadB threadB = new StopThreadB(service);
        threadB.start();

        // 强制停止线程A
        //threadA.stop();
        System.out.println("stop()执行后username和password的结果为：");
        // 利用抛出异常的方式实现停止线程更为安全
        //throw new RuntimeException();
    }
}
