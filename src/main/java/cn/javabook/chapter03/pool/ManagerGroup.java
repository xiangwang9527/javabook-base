package cn.javabook.chapter03.pool;

import java.util.concurrent.*;

/**
 * 项目组
 *
 */
public class ManagerGroup {
    private static ExecutorService projectGroup = new ThreadPoolExecutor(
            5, // corePoolSize：核心小队数量
            20, // maximumPoolSize：最多能容纳多少个小队
            30, // keepAliveTime：多久没活干就请出项目组
            TimeUnit.SECONDS, // unit：时间单位
            new ArrayBlockingQueue<Runnable>(5),// workQueue：当前已有多少个包工头就不再接收入组申请
            new ThreadPoolExecutor.CallerRunsPolicy() // handler：项目组拒绝包工头加入时怎么处理
    );

    // 项目组增加工作小队
    public static void addTask(Manager manager) {
        projectGroup.execute(manager);
    }

    public static void main(String[] args) {
        Manager manager1 = new Manager();
        Worker worker1 = new Worker();
        manager1.setWorker(worker1);

        Manager manager2 = new Manager();
        Worker worker2 = new Worker();
        manager2.setWorker(worker2);

        Manager manager3 = new Manager();
        Worker worker3 = new Worker();
        manager3.setWorker(worker3);

        // 申请进入项目组有活干才可能不被"优化"
        ManagerGroup.addTask(manager1);
        ManagerGroup.addTask(manager2);
        ManagerGroup.addTask(manager3);
    }
}
