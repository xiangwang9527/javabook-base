package cn.javabook.chapter03.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 带注释的ThreadPoolExecutor
 *
 */
public class ThreadPoolExecutor1 {
    public ThreadPoolExecutor1(
            /**
             * corePoolSize：初始化时指定的核心线程数，包括空闲线程，必须大于等于0，当有新任务提交时，会执行以下判断（workCount为当前活跃的线程数量）:
             * 当workCount＜ corePoolSize：即使线程池中有空闲线程，也会创建新线程
             * 当corePoolSize ≤ workCount ＜ maximumPoolSize：只有workQueue满时才创建新线程
             * 当corePoolSize < workCount ＜ maximumPoolSize：且超过corePoolSize部分的线程空闲时间达到keepAliveTime时，就回收这些线程，当设置allowCoreThreadTimeOut(true)时，
             *                                               线程池中corePoolSize范围内的线程空闲时间达到keepAliveTime也将被回收
             * 当设置corePoolSize == maximumPoolSize：线程池的大小固定，此时如有新任务提交，且workQueue未满时，会将请求放入workQueue，等待有空闲的线程从workQueue中取任务并处理
             * 当workCount ≥ maximumPoolSize：若workQueue满，则采取handler对应的策略
             */
            int corePoolSize,
            // maximumPoolSize：初始化时指定的最大线程数量
            int maximumPoolSize,
            // keepAliveTime：线程池维护线程所允许的空闲时间。当线程池中的线程数量大于corePoolSize时，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是等待，直到等待的时间超过了keepAliveTime
            long keepAliveTime,
            // 空闲时间单位
            TimeUnit unit,
            /**
             * workQueue：阻塞队列的类型是保存等待执行的任务的阻塞队列，主要有四种提交方式：
             * SynchronousQueue：同步队列，这个“队列”内部只包含了一个元素，队列的size始终为0，每执行一个put，就需要一个take来解除阻塞，反之也一样。饱和状态下，线程池能处理的最大线程数量为maximumPoolSize
             *      使用SynchronousQueue队列，提交的任务不会保存，而是会马上提交执行
             *      需要对程序的并发量有个准确的评估，才能设置合适的maximumPoolSize数量，否则很容易就会执行拒绝策略
             * ArrayBlockingQueue：有界任务队列，饱和状态下，线程池能处理的最大线程数量为maximumPoolSize + ArrayBlockingQueue.SIZE
             * LinkedBlockingQueue：无界任务队列，线程池的任务队列可以无限制的添加新的任务，此时线程池能够创建的最大线程数是corePoolSize，
             *                      而maximumPoolSize就无效了，线程池饱和状态下能处理的最大线程数量只取决于系统的性能
             * PriorityBlockingQueue：优先任务队列，同LinkedBlockingQueue一样，它也是一个无界的任务队列，只不过需要自己实现元素的Comparable排序接口
             */
            BlockingQueue<Runnable> workQueue,
            // threadFactory：创建新线程，使新创建的线程有相同的优先级且为非守护线程，同时设置线程的名称，默认使用Executors.DefaultThreadFactory类创建
            ThreadFactory threadFactory,
            /**
             * handler：表示线程池的饱和策略，意思就是如果阻塞队列满了并且没有空闲的线程，此时如果继续提交任务，就需要采取一种策略处理该任务，线程池提供了4种策略
             * AbortPolicy：直接抛出异常，这是默认策略
             * CallerRunsPolicy：如果线程池的线程数量达到上限，则把任务队列中的任务放在调用者的线程当运行
             * DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务
             * DiscardPolicy：直接丢弃任务
             */
            RejectedExecutionHandler handler) {
        // balabala… …
    }
}
