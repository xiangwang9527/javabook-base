package cn.javabook.chapter03.demo;

/**
 * 线程B
 *
 */
public class StopThreadB extends Thread {
    private final StopService service;

    public StopThreadB(StopService service) {
        this.service = service;
    }

    @Override
    public void run() {
        System.out.println("username = " + service.getUsername());
        System.out.println("password = " + service.getPassword());
    }
}
