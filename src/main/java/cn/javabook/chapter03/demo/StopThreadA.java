package cn.javabook.chapter03.demo;

/**
 * 线程A
 *
 */
public class StopThreadA extends Thread {
    private final StopService service;

    public StopThreadA(StopService service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.printString("StopThreadA", "123456");
    }
}
