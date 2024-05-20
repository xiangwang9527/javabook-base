package cn.javabook.chapter03.pool;

/**
 * 包工头
 *
 */
public class Manager implements Runnable {
    private Worker worker;

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    @Override
    public void run() {
        worker.dosomething();
    }
}
