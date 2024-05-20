///**
// * 由Java大神Doug Lea编写此类。并附上JSR规范
// *
// * https://docs.oracle.com/javase/specs/index.html
// */
//
//package com.java.book.chapter03.lockaqs;
//
//import java.util.concurrent.TimeUnit;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import sun.misc.Unsafe;
//
///**
// * Provides a framework for implementing blocking locks and related
// * synchronizers (semaphores, events, etc) that rely on first-in-first-out
// * (FIFO) wait queues. 提供了一个实现阻塞锁和依赖FIFO的等待队列的相关的同步器（信号灯、事件等）框架
// *
// * This class is designed to be a useful basis for most kinds of synchronizers
// * that rely on a single atomic {@code int} value to represent state.
// * 这个类对于大多数使用一个单独原子类的int值来表示状态的同步器很有用
// *
// * Subclasses must define the protected methods that change this state, and
// * which define what that state means in terms of this object being acquired or
// * released. 子类必须定义protected方法来改变这个状态值，并且定义状态值是获取还是释放对象
// *
// * Given these, the other methods in this class carry out all queuing and
// * blocking mechanics. 鉴于此，这个类中的其他方法实现了所有排队和阻塞的机制
// *
// * Subclasses can maintain other state fields, but only the atomically updated
// * {@code int} value manipulated using methods {@link #getState},
// * {@link #setState} and {@link #compareAndSetState} is tracked with respect to
// * synchronization. 子类可以维护其他的状态值字段，但只有getState、setState和compareAndSetState
// * 方法是通过原子更新来实现同步的
// *
// * <p>
// * Subclasses should be defined as non-public internal helper classes that are
// * used to implement the synchronization properties of their enclosing class.
// * 子类应该定义成非public的内部helper工具类，用于实现其封闭类的同步属性
// *
// * Class {@code AbstractQueuedSynchronizer} does not implement any
// * synchronization interface. AbstractQueuedSynchronizer类没有实现任何同步接口
// *
// * Instead it defines methods such as {@link #acquireInterruptibly} that can be
// * invoked as appropriate by concrete locks and related synchronizers to
// * implement their public methods.
// * 取而代之的是，它定义了像acquireInterruptibly这样的方法，通过调用恰当的具体 锁和相关同步器方法，以便实现他们自己的公共方法
// *
// * <p>
// * This class supports either or both a default <em>exclusive</em> mode and a
// * <em>shared</em> mode. 这个类既支持默认的独占模式，也支持共享模式，也支持两种模式一起实现
// *
// * When acquired in exclusive mode, attempted acquires by other threads cannot
// * succeed. 当在独占模式获取到锁时，其他线程再尝试获取锁会失败
// *
// * Shared mode acquires by multiple threads may (but need not) succeed.
// * 共享模式，多个线程都能成功获取到锁
// *
// * This class does not understand these differences except in the mechanical
// * sense that when a shared mode acquire succeeds, the next waiting thread (if
// * one exists) must also determine whether it can acquire as well.
// * 这个类不会理解机制的不同，共享模式中的一个线程获取锁成功了，下一个线程 （如果存在）仍然会去确定它自己是否也可以获取
// *
// * Threads waiting in the different modes share the same FIFO queue.
// * 线程虽在不同的模式中，却都在等待共享相同的FIFO队列
// *
// * Usually, implementation subclasses support only one of these modes, but both
// * can come into play for example in a {@link ReadWriteLock}.
// * 通常，子类只需要实现这两种模式中的一种，但也能两种都实现，例如ReadWriteLock
// *
// * Subclasses that support only exclusive or only shared modes need not define
// * the methods supporting the unused mode. 仅支持一种模式的子类，不必定义另一种模式下的方法
// *
// * <p>
// * This class defines a nested {@link ConditionObject} class that can be used as
// * a {@link Condition} implementation by subclasses supporting exclusive mode
// * for which method {@link#isHeldExclusively} reports whether synchronization is
// * exclusively held with respect to the current thread, method {@link #release}
// * invoked with the current {@link #getState} value fully releases this object,
// * and {@link #acquire}, given this saved state value, eventually restores this
// * object to its previous acquired state.
// * 这个类定义了一个嵌套的ConditionObject类，该类可以被支持独占模式的子类用作
// * Condition实现，为此，isHeldExclusively()报告当前线程是否持续保持同步，
// * release方法通过调用getState来完全释放当前对象，并且将当前的资源状态 再保存到state中，最后会将此对象恢复为先前的获取状态
// *
// * No {@code AbstractQueuedSynchronizer} method otherwise creates such a
// * condition, so if this constraint cannot be met, do not use it.
// * 没有AbstractQueuedSynchronizer方法去创建condition，因此如果不能满足这个约束，就不要使用它
// *
// * The behavior of {@link ConditionObject} depends of course on the semantics of
// * its synchronizer implementation. ConditionObject的行为依赖于其同步器实现的语义
// *
// * <p>
// * This class provides inspection, instrumentation, and monitoring methods for
// * the internal queue, as well as similar methods for condition objects.
// * 这个类提供检查、追踪和监控内部队列的方法，类似于condition对象的方法
// *
// * These can be exported as desired into classes using an
// * {@code AbstractQueuedSynchronizer} for their synchronization mechanics.
// * 可以根据需要使用AbstractQueuedSynchronizer，将它们导入到类中以实现其同步机制
// *
// * <p>
// * Serialization of this class stores only the underlying atomic integer
// * maintaining state, so deserialized objects have empty thread queues.
// * 这个类仅序列化state的原子值，因此反序列化出来的对象中的线程队列是空的
// *
// * Typical subclasses requiring serializability will define a {@code readObject}
// * method that restores this to a known initial state upon deserialization.
// * 需要序列化的子类可以在反序列化的时候定义一个readObject方法来恢复已知的初始状态
// *
// *
// * <h3>Usage</h3> 使用
// *
// * <p>
// * To use this class as the basis of a synchronizer, redefine the following
// * methods, as applicable, by inspecting and/or modifying the synchronization
// * state using {@link #getState}, {@link #setState} and/or
// * {@link #compareAndSetState}: 使用这个类作为同步器锁，需要重新定义以下方法：
// *
// * <ul>
// * <li>{@link #tryAcquire}
// * <li>{@link #tryRelease}
// * <li>{@link #tryAcquireShared}
// * <li>{@link #tryReleaseShared}
// * <li>{@link #isHeldExclusively}
// * </ul>
// *
// * Each of these methods by default throws
// * {@link UnsupportedOperationException}.
// * 这些方法默认抛出UnsupportedOperationException异常
// *
// * Implementations of these methods must be internally thread-safe, and should
// * in general be short and not block. 这些方法的实现必须在内部是线程安全的，而且通常都很简短，没有阻塞
// *
// * Defining these methods is the <em>only</em> supported means of using this
// * class. 定义这些方法是使用这个类唯一可行的方式
// *
// * All other methods are declared {@code final} because they cannot be
// * independently varied. 所有其他的方法都被声明为final，因为他们无法独自变化
// *
// * <p>
// * You may also find the inherited methods from
// * {@link AbstractOwnableSynchronizer} useful to keep track of the thread owning
// * an exclusive synchronizer.
// * 你可能也发现了继承自AbstractOwnableSynchronizer的方法对于跟踪拥有独占同步器的线程很有用
// *
// * You are encouraged to use them -- this enables monitoring and diagnostic
// * tools to assist users in determining which threads hold locks.
// * 鼓励你使用它们——这使得监控和诊断工具能够帮助用户确定那些线程持有锁
// *
// * <p>
// * Even though this class is based on an internal FIFO queue, it does not
// * automatically enforce FIFO acquisition policies.
// * 即使这个类是基于一个内部的FIFO队列，它也不会自动执行FIFO获得策略
// *
// * The core of exclusive synchronization takes the form: 独占锁的核心采用以下形式：
// *
// * <pre>
// * Acquire方法:
// *     while (!tryAcquire(arg)) {
// *        <em>enqueue thread if it is not already queued</em>;
// *        使线程入队，如果它还没有在队列中的话
// *        <em>possibly block current thread</em>;
// *        可能会阻塞当前线程
// *     }
// *
// * Release方法:
// *     if (tryRelease(arg))
// *        <em>unblock the first queued thread</em>;
// *        解锁队列中的第一个线程
// * </pre>
// *
// * (Shared mode is similar but may involve cascading signals.) 共享模式类似，但可能涉及级联信号
// *
// * <p id="barging">
// * Because checks in acquire are invoked before enqueuing, a newly acquiring
// * thread may <em>barge</em> ahead of others that are blocked and queued.
// * 因为进入队列之前检查锁的获取，因此一个新的线程可能会插入其他阻塞或排队的线程之前
// *
// * However, you can, if desired, define {@code tryAcquire} and/or
// * {@code tryAcquireShared} to disable barging by internally invoking one or
// * more of the inspection methods, thereby providing a <em>fair</em> FIFO
// * acquisition order. 但如果你愿意的话，可以定义tryAcquire和/或tryAcquireShared方法禁止插队，从而提供
// * 一个公平的获取顺序
// *
// * In particular, most fair synchronizers can define {@code tryAcquire} to
// * return {@code false} if {@link #hasQueuedPredecessors} (a method specifically
// * designed to be used by fair synchronizers) returns {@code true}.
// * 尤其是，如果hasQueuedPredecessors（专用于公平锁的方法）返回true，大多数公平锁 可以定义tryAcquire方法返回false
// *
// * Other variations are possible. 其他变化也是可能的
// *
// * <p>
// * Throughput and scalability are generally highest for the default barging
// * (also known as <em>greedy</em>, <em>renouncement</em>, and
// * <em>convoy-avoidance</em>) strategy.
// * 对于默认插入（也称为greedy，renouncement和convoy-avoidance）策略， 吞吐量和可扩展性通常是最高的
// *
// * While this is not guaranteed to be fair or starvation-free, earlier queued
// * threads are allowed to recontend before later queued threads, and each
// * recontention has an unbiased chance to succeed against incoming threads.
// * 尽管这不能保证公平，也不能保证没有饥饿，但是可以让较早排队的线程在较 晚排队的线程之前进行重新竞争
// *
// * Also, while acquires do not spin in the usual sense, they may perform
// * multiple invocations of {@code tryAcquire} interspersed with other
// * computations before blocking.
// * 同样，尽管获得锁通常不会自旋，但它们在阻塞之前，可以执行多个对tryAcquire的调用与其他阻塞前的计算
// *
// * This gives most of the benefits of spins when exclusive synchronization is
// * only briefly held, without most of the liabilities when it isn't.
// * 这提供了自旋的大部分好处，而在不进行排他同步时，也不会带来很多负担
// *
// * If so desired, you can augment this by preceding calls to acquire methods
// * with "fast-path" checks, possibly prechecking {@link #hasContended} and/or
// * {@link #hasQueuedThreads} to only do so if the synchronizer is likely not to
// * be contended. 如果需要，你可以通过在调用之前对获取方法进行“快速路径”检查来增强此功能，
// * 可能会预先检查hasContended和/或hasQueuedThreads
// *
// * <p>
// * This class provides an efficient and scalable basis for synchronization in
// * part by specializing its range of use to synchronizers that can rely on
// * {@code int} state, acquire, and release parameters, and an internal FIFO wait
// * queue. 此类为同步提供了有效且可扩展的基础，部分原因是依赖于使用state，获取和释放参数 以及内部FIFO等待队列的同步器
// *
// * When this does not suffice, you can build synchronizers from a lower level
// * using {@link java.util.concurrent.atomic atomic} classes, your own custom
// * {@link java.util.Queue} classes, and {@link LockSupport} blocking support.
// * 如果这不够，你可以使用原子类、实现Queue接口和LockSupport提供低级别的阻塞支持
// *
// * <h3>Usage Examples</h3> 使用示例
// *
// * <p>
// * Here is a non-reentrant mutual exclusion lock class that uses the value zero
// * to represent the unlocked state, and one to represent the locked state.
// * 这是一个非重入互斥独占锁类，使用0表示非锁定状态，1表示锁定状态
// *
// * While a non-reentrant lock does not strictly require recording of the current
// * owner thread, this class does so anyway to make usage easier to monitor.
// * 而非重入锁并不严格要求记录当前所有者线程，无论如何，这样做是为了更易于使用
// *
// * It also supports conditions and exposes one of the instrumentation methods:
// * 它也支持conditions并公开了一种检测方法：
// *
// * <pre>
// *  {@code
// * class Mutex implements Lock, java.io.Serializable {
// *
// *   // Our internal helper class
// *   // 内部helper类
// *   private static class Sync extends AbstractQueuedSynchronizer {
// *     // Reports whether in locked state
// *     // 是否持有锁
// *     protected boolean isHeldExclusively() {
// *       return getState() == 1;
// *     }
// *
// *     // Acquires the lock if state is zero
// *     // 如果state是0就获得锁
// *     public boolean tryAcquire(int acquires) {
// *       assert acquires == 1; // Otherwise unused 断言acquires=1，否则退出
// *       if (compareAndSetState(0, 1)) {
// *         setExclusiveOwnerThread(Thread.currentThread());
// *         return true;
// *       }
// *       return false;
// *     }
// *
// *     // Releases the lock by setting state to zero
// *     // 通过设置state=0来释放锁
// *     protected boolean tryRelease(int releases) {
// *       assert releases == 1; // Otherwise unused 断言acquires=1，否则退出
// *       if (getState() == 0) throw new IllegalMonitorStateException();
// *       setExclusiveOwnerThread(null);
// *       setState(0);
// *       return true;
// *     }
// *
// *     // Provides a Condition
// *     Condition newCondition() {
// *         return new ConditionObject();
// *     }
// *
// *     // Deserializes properly
// *     // 反序列化
// *     private void readObject(ObjectInputStream s)
// *         throws IOException, ClassNotFoundException {
// *       s.defaultReadObject();
// *       setState(0); // reset to unlocked state
// *     }
// *   }
// *
// *   // The sync object does all the hard work. We just forward to it.
// *   // 同步对象完成了所有困难的工作，我们只需要利用它实现下面的方法
// *
// *   private final Sync sync = new Sync();
// *
// *   public void lock()                { sync.acquire(1); }
// *   public boolean tryLock()          { return sync.tryAcquire(1); }
// *   public void unlock()              { sync.release(1); }
// *   public Condition newCondition()   { return sync.newCondition(); }
// *   public boolean isLocked()         { return sync.isHeldExclusively(); }
// *   public boolean hasQueuedThreads() { return sync.hasQueuedThreads(); }
// *   public void lockInterruptibly() throws InterruptedException {
// *   	sync.acquireInterruptibly(1);
// *   }
// *   public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
// *   	return sync.tryAcquireNanos(1, unit.toNanos(timeout));
// *   }
// * }}
// * </pre>
// *
// * <p>
// * Here is a latch class that is like a
// * {@link java.util.concurrent.CountDownLatch CountDownLatch} except that it
// * only requires a single {@code signal} to fire.
// * 这是一个和CountDownLatch类很像的latch类，除了它仅仅需要一个获取信号启动外
// *
// * Because a latch is non-exclusive, it uses the {@code shared} acquire and
// * release methods. 因为latch类是一个非独占锁，它使用共享的获取和释放方法
// *
// * <pre>
// * {
// * 	&#64;code
// * 	class BooleanLatch {
// *
// * 		private static class Sync extends AbstractQueuedSynchronizer {
// * 			boolean isSignalled() {
// * 				return getState() != 0;
// * 			}
// *
// * 			protected int tryAcquireShared(int ignore) {
// * 				return isSignalled() ? 1 : -1;
// * 			}
// *
// * 			protected boolean tryReleaseShared(int ignore) {
// * 				setState(1);
// * 				return true;
// * 			}
// * 		}
// *
// * 		private final Sync sync = new Sync();
// *
// * 		public boolean isSignalled() {
// * 			return sync.isSignalled();
// * 		}
// *
// * 		public void signal() {
// * 			sync.releaseShared(1);
// * 		}
// *
// * 		public void await() throws InterruptedException {
// * 			sync.acquireSharedInterruptibly(1);
// * 		}
// * 	}
// * }
// * </pre>
// *
// * @since 1.5
// * @author Doug Lea
// */
//public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
//	private static final long serialVersionUID = 7373984972572414691L;
//
//	/**
//	 * Creates a new {@code AbstractQueuedSynchronizer} instance with initial
//	 * synchronization state of zero.
//	 */
//	/**
//	 * 用0初始化state同步状态，创建一个新的AbstractQueuedSynchronizer实例
//	 */
//	protected AbstractQueuedSynchronizer() {
//	}
//
//	/**
//	 * Wait queue node class. 等待队列的Node类
//	 *
//	 * <p>
//	 * The wait queue is a variant of a "CLH" (Craig, Landin, and Hagersten) lock
//	 * queue. 等待队列是CLH锁队列的变体
//	 *
//	 * CLH locks are normally used for spinlocks. CLH锁通常用于自旋锁
//	 *
//	 * We instead use them for blocking synchronizers, but use the same basic tactic
//	 * of holding some of the control information about a thread in the predecessor
//	 * of its node. 我们将用他们用于阻塞同步器，但使用相同的基本策略， 将有关线程的某些控制信息保存在其节点的前继节点中
//	 *
//	 * A "status" field in each node keeps track of whether a thread should block.
//	 * 每个节点中的“status”字段都保持线程是否应该阻塞的状态
//	 *
//	 * A node is signalled when its predecessor releases. 当节点的前继释放时，会给当前节点发信号
//	 *
//	 * Each node of the queue otherwise serves as a specific-notification-style
//	 * monitor holding a single waiting thread. The status field does NOT control
//	 * whether threads are granted locks etc though. A thread may try to acquire if
//	 * it is first in the queue. But being first does not guarantee success; it only
//	 * gives the right to contend. So the currently released contender thread may
//	 * need to rewait.
//	 *
//	 * <p>
//	 * To enqueue into a CLH lock, you atomically splice it in as new tail. To
//	 * dequeue, you just set the head field.
//	 *
//	 * <pre>
//	 *      +------+  prev +-----+       +-----+
//	 * head |      | <---- |     | <---- |     |  tail
//	 *      +------+       +-----+       +-----+
//	 * </pre>
//	 *
//	 * <p>
//	 * Insertion into a CLH queue requires only a single atomic operation on "tail",
//	 * so there is a simple atomic point of demarcation from unqueued to queued.
//	 * Similarly, dequeuing involves only updating the "head". However, it takes a
//	 * bit more work for nodes to determine who their successors are, in part to
//	 * deal with possible cancellation due to timeouts and interrupts.
//	 * 插入到CLH队列中只需要对tail执行一次原子操作，因此存在一个简单的原子分界点，即从未排队到排队
//	 * 同样，出队仅涉及更新head。但是，节点需要花费更多的精力来确定其后继者是谁，
//	 * 部分原因是要处理由于超时和中断而可能导致的取消
//	 *
//	 * <p>
//	 * The "prev" links (not used in original CLH locks), are mainly needed to
//	 * handle cancellation. If a node is cancelled, its successor is (normally)
//	 * relinked to a non-cancelled predecessor. For explanation of similar mechanics
//	 * in the case of spin locks, see the papers by Scott and Scherer at
//	 * http://www.cs.rochester.edu/u/scott/synchronization/
//	 *
//	 * <p>
//	 * We also use "next" links to implement blocking mechanics. The thread id for
//	 * each node is kept in its own node, so a predecessor signals the next node to
//	 * wake up by traversing next link to determine which thread it is.
//	 * Determination of successor must avoid races with newly queued nodes to set
//	 * the "next" fields of their predecessors. This is solved when necessary by
//	 * checking backwards from the atomically updated "tail" when a node's successor
//	 * appears to be null. (Or, said differently, the next-links are an optimization
//	 * so that we don't usually need a backward scan.)
//	 *
//	 * <p>
//	 * Cancellation introduces some conservatism to the basic algorithms. Since we
//	 * must poll for cancellation of other nodes, we can miss noticing whether a
//	 * cancelled node is ahead or behind us. This is dealt with by always unparking
//	 * successors upon cancellation, allowing them to stabilize on a new
//	 * predecessor, unless we can identify an uncancelled predecessor who will carry
//	 * this responsibility.
//	 *
//	 * <p>
//	 * CLH queues need a dummy header node to get started. But we don't create them
//	 * on construction, because it would be wasted effort if there is never
//	 * contention. Instead, the node is constructed and head and tail pointers are
//	 * set upon first contention.
//	 *
//	 * <p>
//	 * Threads waiting on Conditions use the same nodes, but use an additional link.
//	 * Conditions only need to link nodes in simple (non-concurrent) linked queues
//	 * because they are only accessed when exclusively held. Upon await, a node is
//	 * inserted into a condition queue. Upon signal, the node is transferred to the
//	 * main queue. A special value of status field is used to mark which queue a
//	 * node is on.
//	 *
//	 * <p>
//	 * Thanks go to Dave Dice, Mark Moir, Victor Luchangco, Bill Scherer and Michael
//	 * Scott, along with members of JSR-166 expert group, for helpful ideas,
//	 * discussions, and critiques on the design of this class.
//	 */
//	static final class Node {
//		/** Marker to indicate a node is waiting in shared mode */
//		static final Node SHARED = new Node();
//		/** Marker to indicate a node is waiting in exclusive mode */
//		static final Node EXCLUSIVE = null;
//		/** waitStatus value to indicate thread has cancelled */
//		static final int CANCELLED = 1;
//		/** waitStatus value to indicate successor's thread needs unparking */
//		static final int SIGNAL = -1;
//		/** waitStatus value to indicate thread is waiting on condition */
//		static final int CONDITION = -2;
//		/**
//		 * waitStatus value to indicate the next acquireShared should unconditionally
//		 * propagate
//		 */
//		static final int PROPAGATE = -3;
//
//		/**
//		 * Status field, taking on only the values:
//		 *
//		 * SIGNAL: The successor of this node is (or will soon be) blocked (via park),
//		 * so the current node must unpark its successor when it releases or cancels.
//		 * To avoid races, acquire methods must first indicate they need a signal,
//		 * then retry the atomic acquire, and then, on failure, block.
//		 * 值为-1，表示当前节点的的后继节点将要或者已经被阻塞，在当前节点释放的时候需要unpark（唤醒）后继节点
//		 *
//		 * CANCELLED: This node is cancelled due to timeout or interrupt. Nodes never
//		 * leave this state. In particular, a thread with cancelled node never again blocks.
//		 * 值为1，表示当前节点被取消
//		 *
//		 * CONDITION: This node is currently on a condition queue. It will not be used
//		 * as a sync queue node until transferred, at which time the status will be set to 0.
//		 * (Use of this value here has nothing to do with the other uses of the field,
//		 * but simplifies mechanics.)
//		 * 值为-2，表示当前节点在等待condition，即在condition队列中
//		 *
//		 * PROPAGATE: A releaseShared should be propagated to other nodes. This is set
//		 * (for head node only) in doReleaseShared to ensure propagation continues, even
//		 * if other operations have since intervened. 0: None of the above
//		 * 值为-3，表示releaseShared需要被传播给后续节点（仅在共享模式下使用）
//		 *
//		 * The values are arranged numerically to simplify use. Non-negative values mean
//		 * that a node doesn't need to signal. So, most code doesn't need to check for
//		 * particular values, just for sign.
//		 *
//		 * The field is initialized to 0 for normal sync nodes, and CONDITION for
//		 * condition nodes. It is modified using CAS (or when possible, unconditional
//		 * volatile writes).
//		 * 无状态，表示当前节点在队列中等待获取锁
//		 *
//		 */
//		volatile int waitStatus;
//
//		/**
//		 * Link to predecessor node that current node/thread relies on for checking
//		 * waitStatus. Assigned during enqueuing, and nulled out (for sake of GC) only
//		 * upon dequeuing. Also, upon cancellation of a predecessor, we short-circuit
//		 * while finding a non-cancelled one, which will always exist because the head
//		 * node is never cancelled: A node becomes head only as a result of successful
//		 * acquire. A cancelled thread never succeeds in acquiring, and a thread only
//		 * cancels itself, not any other node.
//		 */
//		volatile Node prev;
//
//		/**
//		 * Link to the successor node that the current node/thread unparks upon release.
//		 * Assigned during enqueuing, adjusted when bypassing cancelled predecessors,
//		 * and nulled out (for sake of GC) when dequeued. The enq operation does not
//		 * assign next field of a predecessor until after attachment, so seeing a null
//		 * next field does not necessarily mean that node is at end of queue. However,
//		 * if a next field appears to be null, we can scan prev's from the tail to
//		 * double-check. The next field of cancelled nodes is set to point to the node
//		 * itself instead of null, to make life easier for isOnSyncQueue.
//		 */
//		volatile Node next;
//
//		/**
//		 * The thread that enqueued this node. Initialized on construction and nulled
//		 * out after use.
//		 */
//		volatile Thread thread;
//
//		/**
//		 * Link to next node waiting on condition, or the special value SHARED.
//		 * Because condition queues are accessed only when holding in exclusive mode, we just
//		 * need a simple linked queue to hold nodes while they are waiting on
//		 * conditions. They are then transferred to the queue to re-acquire. And because
//		 * conditions can only be exclusive, we save a field by using special value to
//		 * indicate shared mode.
//		 */
//		Node nextWaiter;
//
//		/**
//		 * Returns true if node is waiting in shared mode.
//		 */
//		final boolean isShared() {
//			return nextWaiter == SHARED;
//		}
//
//		/**
//		 * Returns previous node, or throws NullPointerException if null. Use when
//		 * predecessor cannot be null. The null check could be elided, but is present to
//		 * help the VM.
//		 * 返回前继节点，如果为空则抛出异常
//		 *
//		 * @return the predecessor of this node
//		 */
//		final Node predecessor() throws NullPointerException {
//			Node p = prev;
//			if (p == null) {
//				throw new NullPointerException();
//			} else {
//				return p;
//			}
//		}
//
//		Node() { // Used to establish initial head or SHARED marker
//		}
//
//		Node(Thread thread, Node mode) { // Used by addWaiter
//			this.nextWaiter = mode;
//			this.thread = thread;
//		}
//
//		Node(Thread thread, int waitStatus) { // Used by Condition
//			this.waitStatus = waitStatus;
//			this.thread = thread;
//		}
//	}
//
//	/**
//	 * Head of the wait queue, lazily initialized. Except for initialization, it is
//	 * modified only via method setHead. Note: If head exists, its waitStatus is
//	 * guaranteed not to be CANCELLED. 等待队列头部节点，懒加载，它仅仅通过setHead方法修改
//	 * 注意：如果头部节点存在，它的等待状态不保证会是CANCELLED
//	 */
//	private transient volatile Node head;
//
//	/**
//	 * Tail of the wait queue, lazily initialized. Modified only via method enq to
//	 * add new wait node. 等待队列的队尾节点，懒加载，只能通过enq方法加载新节点到队尾
//	 */
//	private transient volatile Node tail;
//
//	/**
//	 * The synchronization state. 同步状态
//	 * 该变量对不同的子类实现具有不同的意义
//	 * 对ReentrantLock来说，它表示加锁的状态：
//	 * 无锁时state=0，有锁时state>0
//	 * 第一次加锁时，将state+1
//	 * 而对于CountDownLatch来说，它是初始化时子线程的数量
//	 *
//	 */
//	private volatile int state;
//
//	/**
//	 * Returns the current value of synchronization state. This operation has memory
//	 * semantics of a {@code volatile} read.
//	 *
//	 * @return current state value
//	 */
//	protected final int getState() {
//		return state;
//	}
//
//	/**
//	 * Sets the value of synchronization state. This operation has memory semantics
//	 * of a {@code volatile} write.
//	 *
//	 * @param newState the new state value
//	 */
//	protected final void setState(int newState) {
//		state = newState;
//	}
//
//	/**
//	 * Atomically sets synchronization state to the given updated value if the
//	 * current state value equals the expected value. This operation has memory
//	 * semantics of a {@code volatile} read and write. 以原子方式设置同步状态为指定的值
//	 *
//	 * @param expect the expected value
//	 * @param update the new value
//	 * @return {@code true} if successful. False return indicates that the actual
//	 *         value was not equal to the expected value.
//	 */
//	protected final boolean compareAndSetState(int expect, int update) {
//		// See below for intrinsics setup to support this
//		return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
//	}
//
//	// Queuing utilities
//
//	/**
//	 * The number of nanoseconds for which it is faster to spin rather than to use
//	 * timed park. A rough estimate suffices to improve responsiveness with very
//	 * short timeouts. 自旋超时时间，使用比park更快的纳秒，足以在非常短的时间内提高响应能力，默认值1000纳秒
//	 */
//	static final long spinForTimeoutThreshold = 1000L;
//
//	/**
//	 * Inserts node into queue, initializing if necessary. See picture above.
//	 * 插入节点到队尾，如果有必要的话初始化
//	 *
//	 * @param node the node to insert
//	 * @return node's predecessor
//	 */
//	private Node enq(final Node node) {
//		// 自旋
//		for (;;) {
//			// 将队尾指针给当前节点
//			Node t = tail;
//			if (t == null) { // Must initialize 必须初始化
//				// 如果尾节点为null，说明队列还没有任何节点，那么头节点也就是尾节点
//				if (compareAndSetHead(new Node())) {
//					tail = head;
//				}
//			} else {
//				// 否则尾节点成为当前待加入节点的前继节点
//				node.prev = t;
//				// 将当前节点设置为尾节点
//				if (compareAndSetTail(t, node)) {
//					// 尾节点的后续节点为当前节点
//					t.next = node;
//					return t;
//				}
//			}
//		}
//	}
//
//	/**
//	 * Creates and enqueues node for current thread and given mode.
//	 * 按给定模式将当前线程包装成一个入队的节点
//	 *
//	 * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
//	 * @return the new node
//	 */
//	private Node addWaiter(Node mode) {
//		// 将当前线程包装成节点
//		Node node = new Node(Thread.currentThread(), mode);
//		// Try the fast path of enq; backup to full enq on failure
//		// 尝试快速入队
//		Node pred = tail;
//		// 尾节点是否为null
//		if (pred != null) {
//			// 将尾节点设置为当前节点的前继节点
//			node.prev = pred;
//			// 将当前节点设置为尾节点
//			if (compareAndSetTail(pred, node)) {
//				// 尾节点的后续节点为当前节点
//				pred.next = node;
//				return node;
//			}
//		}
//		// 尾节点为null，则执行enq
//		enq(node);
//		// 返回当前节点
//		return node;
//	}
//
//	/**
//	 * Sets head of queue to be node, thus dequeuing. Called only by acquire
//	 * methods. Also nulls out unused fields for sake of GC and to suppress
//	 * unnecessary signals and traversals.
//	 * 将节点设置为队列头，从而让持有锁的节点出列，仅由acquire调用
//	 * 为了GC和抑制不必要的信号和遍历，也会清空未使用的字段
//	 *
//	 * @param node the node
//	 */
//	private void setHead(Node node) {
//		// 将节点设置为队列头
//		head = node;
//		// 头节点没有线程
//		node.thread = null;
//		// 头节点没有前继节点
//		node.prev = null;
//	}
//
//	/**
//	 * Wakes up node's successor, if one exists.
//	 * 唤醒节点的后续节点
//	 *
//	 * @param node the node
//	 */
//	private void unparkSuccessor(Node node) {
//		/*
//		 * If status is negative (i.e., possibly needing signal) try to clear in
//		 * anticipation of signalling. It is OK if this fails or if status is changed by
//		 * waiting thread.
//		 * 如果状态值为负，就尝试清除预期信号值
//		 * 如果失败或状态由等待线程更改，则OK
//		 */
//		int ws = node.waitStatus;
//		if (ws < 0) {
//			compareAndSetWaitStatus(node, ws, 0);
//		}
//
//		/*
//		 * Thread to unpark is held in successor, which is normally just the next node.
//		 * But if cancelled or apparently null, traverse backwards from tail to find the
//		 * actual non-cancelled successor.
//		 */
//		Node s = node.next;
//		if (s == null || s.waitStatus > 0) {
//			s = null;
//			for (Node t = tail; t != null && t != node; t = t.prev) {
//				if (t.waitStatus <= 0) {
//					s = t;
//				}
//			}
//		}
//		if (s != null) {
//			LockSupport.unpark(s.thread);
//		}
//	}
//
//	/**
//	 * Release action for shared mode -- signals successor and ensures propagation.
//	 * (Note: For exclusive mode, release just amounts to calling unparkSuccessor of
//	 * head if it needs signal.)
//	 * 共享模式下的释放行为——发出后续信号并确保传播
//	 * （注意：对于独占模式，释放只是在需要信号时调用head的unparkSuccessor方法）
//	 *
//	 */
//	private void doReleaseShared() {
//		/*
//		 * Ensure that a release propagates, even if there are other in-progress
//		 * acquires/releases. This proceeds in the usual way of trying to
//		 * unparkSuccessor of head if it needs signal. But if it does not, status is set
//		 * to PROPAGATE to ensure that upon release, propagation continues.
//		 * Additionally, we must loop in case a new node is added while we are doing
//		 * this. Also, unlike other uses of unparkSuccessor, we need to know if CAS to
//		 * reset status fails, if so rechecking.
//		 */
//		for (;;) {
//			Node h = head;
//			if (h != null && h != tail) {
//				int ws = h.waitStatus;
//				if (ws == Node.SIGNAL) {
//					if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0)) {
//						continue; // loop to recheck cases
//					}
//					unparkSuccessor(h);
//				} else if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE)) {
//					continue; // loop on failed CAS
//				}
//			}
//			if (h == head) {
//				// loop if head changed
//				break;
//			}
//		}
//	}
//
//	/**
//	 * Sets head of queue, and checks if successor may be waiting in shared mode, if
//	 * so propagating if either propagate > 0 or PROPAGATE status was set.
//	 *
//	 * @param node the node
//	 * @param propagate the return value from a tryAcquireShared
//	 */
//	private void setHeadAndPropagate(Node node, int propagate) {
//		Node h = head; // Record old head for check below
//		setHead(node);
//		/*
//		 * Try to signal next queued node if: Propagation was indicated by caller, or
//		 * was recorded (as h.waitStatus either before or after setHead) by a previous
//		 * operation (note: this uses sign-check of waitStatus because PROPAGATE status
//		 * may transition to SIGNAL.) and The next node is waiting in shared mode, or we
//		 * don't know, because it appears null
//		 *
//		 * The conservatism in both of these checks may cause unnecessary wake-ups, but
//		 * only when there are multiple racing acquires/releases, so most need signals
//		 * now or soon anyway.
//		 */
//		if (propagate > 0 || h == null || h.waitStatus < 0 || (h = head) == null || h.waitStatus < 0) {
//			Node s = node.next;
//			if (s == null || s.isShared()) {
//				doReleaseShared();
//			}
//		}
//	}
//
//	// Utilities for various versions of acquire
//
//	/**
//	 * Cancels an ongoing attempt to acquire.
//	 * 取消一个不断尝试获取锁的线程节点
//	 *
//	 * @param node the node
//	 */
//	private void cancelAcquire(Node node) {
//		// Ignore if node doesn't exist
//		if (node == null) {
//			return;
//		}
//
//		node.thread = null;
//
//		// Skip cancelled predecessors
//		Node pred = node.prev;
//		while (pred.waitStatus > 0) {
//			node.prev = pred = pred.prev;
//		}
//
//		// predNext is the apparent node to unsplice. CASes below will
//		// fail if not, in which case, we lost race vs another cancel
//		// or signal, so no further action is necessary.
//		Node predNext = pred.next;
//
//		// Can use unconditional write instead of CAS here.
//		// After this atomic step, other Nodes can skip past us.
//		// Before, we are free of interference from other threads.
//		node.waitStatus = Node.CANCELLED;
//
//		// If we are the tail, remove ourselves.
//		if (node == tail && compareAndSetTail(node, pred)) {
//			compareAndSetNext(pred, predNext, null);
//		} else {
//			// If successor needs signal, try to set pred's next-link
//			// so it will get one. Otherwise wake it up to propagate.
//			int ws;
//			if (pred != head &&
//				((ws = pred.waitStatus) == Node.SIGNAL || (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
//				pred.thread != null) {
//				Node next = node.next;
//				if (next != null && next.waitStatus <= 0) {
//					compareAndSetNext(pred, predNext, next);
//				}
//			} else {
//				unparkSuccessor(node);
//			}
//
//			node.next = node; // help GC
//		}
//	}
//
//	/**
//	 * Checks and updates status for a node that failed to acquire. Returns true if
//	 * thread should block. This is the main signal control in all acquire loops.
//	 * Requires that pred == node.prev.
//	 * 节点获取锁失败时检查并且更新状态值，如果线程应该阻塞返回true
//	 * 在所有获取锁的循环中这是主要的信号控制
//	 *
//	 * @param pred node's predecessor holding status
//	 * @param node the node
//	 * @return {@code true} if thread should block
//	 */
//	private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
//		int ws = pred.waitStatus;
//		if (ws == Node.SIGNAL) {
//			/*
//			 * This node has already set status asking a release to signal it, so it can
//			 * safely park.
//			 */
//			return true;
//		}
//		if (ws > 0) {
//			/*
//			 * Predecessor was cancelled. Skip over predecessors and indicate retry.
//			 */
//			do {
//				node.prev = pred = pred.prev;
//			} while (pred.waitStatus > 0);
//			pred.next = node;
//		} else {
//			/*
//			 * waitStatus must be 0 or PROPAGATE. Indicate that we need a signal, but don't
//			 * park yet. Caller will need to retry to make sure it cannot acquire before
//			 * parking.
//			 */
//			compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
//		}
//
//		return false;
//	}
//
//	/**
//	 * Convenience method to interrupt current thread.
//	 * 中断当前线程的快捷方法
//	 */
//	static void selfInterrupt() {
//		Thread.currentThread().interrupt();
//	}
//
//	/**
//	 * Convenience method to park and then check if interrupted
//	 *
//	 * @return {@code true} if interrupted
//	 */
//	private final boolean parkAndCheckInterrupt() {
//		LockSupport.park(this);
//		return Thread.interrupted();
//	}
//
//	/*
//	 * Various flavors of acquire, varying in exclusive/shared and control modes.
//	 * Each is mostly the same, but annoyingly different. Only a little bit of
//	 * factoring is possible due to interactions of exception mechanics (including
//	 * ensuring that we cancel if tryAcquire throws exception) and other control, at
//	 * least not without hurting performance too much.
//	 * 在独占和共享模式中，获取锁有多种方式，大多数都相同
//	 * 由于异常机制（包括确保在tryAcquire抛出异常时取消）和其他控件的交互，
//	 * 性能可能会受一点影响，但至少不会造成太大的损害
//	 */
//
//	/**
//	 * Acquires in exclusive uninterruptible mode for thread already in queue. Used
//	 * by condition wait methods as well as acquire.
//	 * 以独占不中断模式获取队列中已存在的线程。用于condition等待方法以及获取锁
//	 *
//	 * @param node the node
//	 * @param arg the acquire argument
//	 * @return {@code true} if interrupted while waiting
//	 */
//	final boolean acquireQueued(final Node node, int arg) {
//		boolean failed = true;
//		try {
//			boolean interrupted = false;
//			for (;;) {
//				final Node p = node.predecessor();
//				if (p == head && tryAcquire(arg)) {
//					setHead(node);
//					p.next = null; // help GC
//					failed = false;
//					return interrupted;
//				}
//				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
//					interrupted = true;
//				}
//			}
//		} finally {
//			if (failed) {
//				cancelAcquire(node);
//			}
//		}
//	}
//
//	/**
//	 * Acquires in exclusive interruptible mode.
//	 * 独占中断模式获取锁
//	 *
//	 * @param arg the acquire argument
//	 */
//	private void doAcquireInterruptibly(int arg) throws InterruptedException {
//		final Node node = addWaiter(Node.EXCLUSIVE);
//		boolean failed = true;
//		try {
//			for (;;) {
//				final Node p = node.predecessor();
//				if (p == head && tryAcquire(arg)) {
//					setHead(node);
//					p.next = null; // help GC
//					failed = false;
//					return;
//				}
//				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
//					throw new InterruptedException();
//			}
//		} finally {
//			if (failed)
//				cancelAcquire(node);
//		}
//	}
//
//	/**
//	 * Acquires in exclusive timed mode.
//	 * 独占超时模式获取锁
//	 *
//	 * @param arg the acquire argument
//	 * @param nanosTimeout max wait time
//	 * @return {@code true} if acquired
//	 */
//	private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
//		if (nanosTimeout <= 0L) {
//			return false;
//		}
//		final long deadline = System.nanoTime() + nanosTimeout;
//		final Node node = addWaiter(Node.EXCLUSIVE);
//		boolean failed = true;
//		try {
//			for (;;) {
//				final Node p = node.predecessor();
//				if (p == head && tryAcquire(arg)) {
//					setHead(node);
//					p.next = null; // help GC
//					failed = false;
//					return true;
//				}
//				nanosTimeout = deadline - System.nanoTime();
//				if (nanosTimeout <= 0L) {
//					return false;
//				}
//				if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold) {
//					LockSupport.parkNanos(this, nanosTimeout);
//				}
//				if (Thread.interrupted()) {
//					throw new InterruptedException();
//				}
//			}
//		} finally {
//			if (failed) {
//				cancelAcquire(node);
//			}
//		}
//	}
//
//	/**
//	 * Acquires in shared uninterruptible mode.
//	 * 共享非中断模式获取锁
//	 *
//	 * @param arg the acquire argument
//	 */
//	private void doAcquireShared(int arg) {
//		final Node node = addWaiter(Node.SHARED);
//		boolean failed = true;
//		try {
//			boolean interrupted = false;
//			for (;;) {
//				final Node p = node.predecessor();
//				if (p == head) {
//					int r = tryAcquireShared(arg);
//					if (r >= 0) {
//						setHeadAndPropagate(node, r);
//						p.next = null; // help GC
//						if (interrupted) {
//							selfInterrupt();
//						}
//						failed = false;
//						return;
//					}
//				}
//				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
//					interrupted = true;
//				}
//			}
//		} finally {
//			if (failed) {
//				cancelAcquire(node);
//			}
//		}
//	}
//
//	/**
//	 * Acquires in shared interruptible mode.
//	 * 共享中断模式获取锁
//	 *
//	 * @param arg the acquire argument
//	 */
//	private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
//		final Node node = addWaiter(Node.SHARED);
//		boolean failed = true;
//		try {
//			for (;;) {
//				final Node p = node.predecessor();
//				if (p == head) {
//					int r = tryAcquireShared(arg);
//					if (r >= 0) {
//						setHeadAndPropagate(node, r);
//						p.next = null; // help GC
//						failed = false;
//						return;
//					}
//				}
//				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
//					throw new InterruptedException();
//				}
//			}
//		} finally {
//			if (failed) {
//				cancelAcquire(node);
//			}
//		}
//	}
//
//	/**
//	 * Acquires in shared timed mode.
//	 * 共享超时模式获取锁
//	 *
//	 * @param arg the acquire argument
//	 * @param nanosTimeout max wait time
//	 * @return {@code true} if acquired
//	 */
//	private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
//		if (nanosTimeout <= 0L) {
//			return false;
//		}
//		final long deadline = System.nanoTime() + nanosTimeout;
//		final Node node = addWaiter(Node.SHARED);
//		boolean failed = true;
//		try {
//			for (;;) {
//				final Node p = node.predecessor();
//				if (p == head) {
//					int r = tryAcquireShared(arg);
//					if (r >= 0) {
//						setHeadAndPropagate(node, r);
//						p.next = null; // help GC
//						failed = false;
//						return true;
//					}
//				}
//				nanosTimeout = deadline - System.nanoTime();
//				if (nanosTimeout <= 0L) {
//					return false;
//				}
//				if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold) {
//					LockSupport.parkNanos(this, nanosTimeout);
//				}
//				if (Thread.interrupted()) {
//					throw new InterruptedException();
//				}
//			}
//		} finally {
//			if (failed) {
//				cancelAcquire(node);
//			}
//		}
//	}
//
//	// Main exported methods
//	// 主要的自定义方法
//
//	/**
//	 * Attempts to acquire in exclusive mode. This method should query if the state
//	 * of the object permits it to be acquired in the exclusive mode, and if so to
//	 * acquire it.
//	 * 尝试以独占模式获取锁，此方法应该查询对象的状态state是否允许以独占模式获取锁，如果允许则获取锁
//	 *
//	 * <p>
//	 * This method is always invoked by the thread performing acquire. If this
//	 * method reports failure, the acquire method may queue the thread, if it is not
//	 * already queued, until it is signalled by a release from some other thread.
//	 * This can be used to implement method {@link Lock#tryLock()}.
//	 * 此方法始终由执行获取锁的线程调用，如果获取失败，则会将线程放到CLH队列队尾（如果尚未排队），
//	 * 直到某个其他线程发出释放信号，这可用于实现接口方法tryLock
//	 *
//	 * <p>
//	 * The default implementation throws {@link UnsupportedOperationException}.
//	 * 缺省实现是抛出UnsupportedOperationException异常
//	 *
//	 * @param arg the acquire argument. This value is always the one passed to an
//	 *            acquire method, or is the value saved on entry to a condition
//	 *            wait. The value is otherwise uninterpreted and can represent
//	 *            anything you like.
//	 *            获取锁的参数，表示需要获取锁的数量
//	 *
//	 * @return {@code true} if successful. Upon success, this object has been acquired.
//	 * @throws IllegalMonitorStateException
//	 *             if acquiring would place this synchronizer in an illegal state.
//	 *             This exception must be thrown in a consistent fashion for
//	 *             synchronization to work correctly.
//	 * @throws UnsupportedOperationException
//	 *             if exclusive mode is not supported
//	 */
//	protected boolean tryAcquire(int arg) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Attempts to set the state to reflect a release in exclusive mode.
//	 * 尝试将状态state设置为以独占模式释放锁
//	 *
//	 * <p>
//	 * This method is always invoked by the thread performing release.
//	 * 此方法始终由执行释放的线程调用
//	 *
//	 * <p>
//	 * The default implementation throws {@link UnsupportedOperationException}.
//	 * 缺省实现是抛出UnsupportedOperationException异常
//	 *
//	 * @param arg the release argument. This value is always the one passed to a
//	 *            release method, or the current state value upon entry to a
//	 *            condition wait. The value is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 *            释放锁的参数，表示需要释放锁的数量，与tryAcquire中需要获取的数量一一对应
//	 *
//	 * @return {@code true} if this object is now in a fully released state, so that
//	 *         any waiting threads may attempt to acquire; and {@code false}
//	 *         otherwise.
//	 * @throws IllegalMonitorStateException
//	 *             if releasing would place this synchronizer in an illegal state.
//	 *             This exception must be thrown in a consistent fashion for
//	 *             synchronization to work correctly.
//	 * @throws UnsupportedOperationException
//	 *             if exclusive mode is not supported
//	 */
//	protected boolean tryRelease(int arg) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Attempts to acquire in shared mode. This method should query if the state of
//	 * the object permits it to be acquired in the shared mode, and if so to acquire
//	 * it.
//	 * 共享模式尝试获取锁
//	 *
//	 * <p>
//	 * This method is always invoked by the thread performing acquire. If this
//	 * method reports failure, the acquire method may queue the thread, if it is not
//	 * already queued, until it is signalled by a release from some other thread.
//	 * 此方法始终由执行获取的线程调用，如果调用失败，则会将线程放到CLH队列队尾（如果尚未排队），
//	 * 直到某个其他线程发出释放信号
//	 *
//	 * <p>
//	 * The default implementation throws {@link UnsupportedOperationException}.
//	 * 缺省实现是抛出UnsupportedOperationException异常
//	 *
//	 * @param arg the acquire argument. This value is always the one passed to an
//	 *            acquire method, or is the value saved on entry to a condition
//	 *            wait. The value is otherwise uninterpreted and can represent
//	 *            anything you like.
//	 * @return a negative value on failure; zero if acquisition in shared mode
//	 *         succeeded but no subsequent shared-mode acquire can succeed; and a
//	 *         positive value if acquisition in shared mode succeeded and subsequent
//	 *         shared-mode acquires might also succeed, in which case a subsequent
//	 *         waiting thread must check availability. (Support for three different
//	 *         return values enables this method to be used in contexts where
//	 *         acquires only sometimes act exclusively.) Upon success, this object
//	 *         has been acquired.
//	 * @throws IllegalMonitorStateException
//	 *             if acquiring would place this synchronizer in an illegal state.
//	 *             This exception must be thrown in a consistent fashion for
//	 *             synchronization to work correctly.
//	 * @throws UnsupportedOperationException
//	 *             if shared mode is not supported
//	 */
//	protected int tryAcquireShared(int arg) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Attempts to set the state to reflect a release in shared mode.
//	 * 尝试将状态state设置为以共享模式释放锁
//	 *
//	 * <p>
//	 * This method is always invoked by the thread performing release.
//	 * 此方法始终由执行获取的线程调用
//	 *
//	 * <p>
//	 * The default implementation throws {@link UnsupportedOperationException}.
//	 * 缺省实现是抛出UnsupportedOperationException异常
//	 *
//	 * @param arg the release argument. This value is always the one passed to a
//	 *            release method, or the current state value upon entry to a
//	 *            condition wait. The value is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @return {@code true} if this release of shared mode may permit a waiting
//	 *         acquire (shared or exclusive) to succeed; and {@code false} otherwise
//	 * @throws IllegalMonitorStateException
//	 *             if releasing would place this synchronizer in an illegal state.
//	 *             This exception must be thrown in a consistent fashion for
//	 *             synchronization to work correctly.
//	 * @throws UnsupportedOperationException
//	 *             if shared mode is not supported
//	 */
//	protected boolean tryReleaseShared(int arg) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Returns {@code true} if synchronization is held exclusively with respect to
//	 * the current (calling) thread. This method is invoked upon each call to a
//	 * non-waiting {@link ConditionObject} method. (Waiting methods instead invoke
//	 * {@link #release}.)
//	 * 如果以独占方式保持与当前（调用）线程的同步，则返回true
//	 * 每次调用非等待的ConditionObject方法时都会调用此方法（等待方法改为调用release）
//	 *
//	 * <p>
//	 * The default implementation throws {@link UnsupportedOperationException}. This
//	 * method is invoked internally only within {@link ConditionObject} methods, so
//	 * need not be defined if conditions are not used.
//	 * 缺省实现是抛出UnsupportedOperationException异常
//	 * 此方法仅在ConditionObject内部调用，因此如果不使用Condition，则无需定义
//	 *
//	 * @return {@code true} if synchronization is held exclusively; {@code false}
//	 *         otherwise
//	 * @throws UnsupportedOperationException if conditions are not supported
//	 */
//	protected boolean isHeldExclusively() {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Acquires in exclusive mode, ignoring interrupts. Implemented by invoking at
//	 * least once {@link #tryAcquire}, returning on success. Otherwise the thread is
//	 * queued, possibly repeatedly blocking and unblocking, invoking
//	 * {@link #tryAcquire} until success. This method can be used to implement
//	 * method {@link Lock#lock}.
//	 * 以独占模式获取锁，忽略中断，通过调用至少一次tryAcquire来实现，成功时返回，否则线程将排队
//	 * 可能会反复阻塞和解除阻塞，调用tryAcquire直到成功获取锁，此方法可用于实现接口方法lock
//	 *
//	 * @param arg the acquire argument. This value is conveyed to
//	 *            {@link #tryAcquire} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 */
//	public final void acquire(int arg) {
//		/**
//		 * 该方法主要做了如下工作：
//		 * 先看tryAcquire尝试获取独占锁是否成功，获取成功则返回
//		 * 否则用addWaiter方法将当前线程封装成Node对象，并添加到队列尾部
//		 * 自旋获取锁，并判断中断标志位
//		 * 如果中断标志位为true，则设置中断线程，否则返回
//		 */
//		if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) {
//			selfInterrupt();
//		}
//	}
//
//	/**
//	 * Acquires in exclusive mode, aborting if interrupted. Implemented by first
//	 * checking interrupt status, then invoking at least once {@link #tryAcquire},
//	 * returning on success. Otherwise the thread is queued, possibly repeatedly
//	 * blocking and unblocking, invoking {@link #tryAcquire} until success or the
//	 * thread is interrupted. This method can be used to implement method
//	 * {@link Lock#lockInterruptibly}.
//	 * 独占模式获取锁，如果中断则取消
//	 * 首先检查中断状态，然后至少调用一次tryAcquire来实现方法，在成功时返回，否则线程将进入队尾
//	 * 可能会反复阻塞和解除阻塞，调用tryAcquire，直到成功或线程被中断
//	 * 此方法可用于实现接口方法lockInterruptibly
//	 *
//	 * @param arg the acquire argument. This value is conveyed to
//	 *            {@link #tryAcquire} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @throws InterruptedException if the current thread is interrupted
//	 */
//	public final void acquireInterruptibly(int arg) throws InterruptedException {
//		if (Thread.interrupted()) {
//			throw new InterruptedException();
//		}
//		if (!tryAcquire(arg)) {
//			doAcquireInterruptibly(arg);
//		}
//	}
//
//	/**
//	 * Attempts to acquire in exclusive mode, aborting if interrupted, and failing
//	 * if the given timeout elapses. Implemented by first checking interrupt status,
//	 * then invoking at least once {@link #tryAcquire}, returning on success.
//	 * Otherwise, the thread is queued, possibly repeatedly blocking and unblocking,
//	 * invoking {@link #tryAcquire} until success or the thread is interrupted or
//	 * the timeout elapses. This method can be used to implement method
//	 * {@link Lock#tryLock(long, TimeUnit)}.
//	 * 尝试以独占模式获取锁，如果中断则中止，如果超时则失败
//	 * 通过首先检查中断状态，然后至少调用一次tryAcquire来实现，在成功时返回，否则线程将进入队尾
//	 * 可能会反复阻塞和解除阻塞，调用tryAcquire，直到成功或线程中断或超时结束
//	 * 此方法可用于实现接口方法tryLock(long, TimeUnit)
//	 *
//	 * @param arg the acquire argument. This value is conveyed to
//	 *            {@link #tryAcquire} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @param nanosTimeout the maximum number of nanoseconds to wait
//	 * @return {@code true} if acquired; {@code false} if timed out
//	 * @throws InterruptedException if the current thread is interrupted
//	 */
//	public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
//		if (Thread.interrupted()) {
//			throw new InterruptedException();
//		}
//
//		return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
//	}
//
//	/**
//	 * Releases in exclusive mode. Implemented by unblocking one or more threads if
//	 * {@link #tryRelease} returns true. This method can be used to implement method
//	 * {@link Lock#unlock}.
//	 * 独占模式时释放锁，通过解除一个或多个阻塞线程来实现，如果tryRelease返回true
//	 * 此方法可用于实现接口方法unlock
//	 *
//	 * @param arg the release argument. This value is conveyed to
//	 *            {@link #tryRelease} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @return the value returned from {@link #tryRelease}
//	 */
//	public final boolean release(int arg) {
//		if (tryRelease(arg)) {
//			Node h = head;
//			if (h != null && h.waitStatus != 0) {
//				unparkSuccessor(h);
//			}
//			return true;
//		}
//
//		return false;
//	}
//
//	/**
//	 * Acquires in shared mode, ignoring interrupts. Implemented by first invoking
//	 * at least once {@link #tryAcquireShared}, returning on success. Otherwise the
//	 * thread is queued, possibly repeatedly blocking and unblocking, invoking
//	 * {@link #tryAcquireShared} until success.
//	 *
//	 * @param arg the acquire argument. This value is conveyed to
//	 *            {@link #tryAcquireShared} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 */
//	public final void acquireShared(int arg) {
//		if (tryAcquireShared(arg) < 0) {
//			doAcquireShared(arg);
//		}
//	}
//
//	/**
//	 * Acquires in shared mode, aborting if interrupted. Implemented by first
//	 * checking interrupt status, then invoking at least once
//	 * {@link #tryAcquireShared}, returning on success. Otherwise the thread is
//	 * queued, possibly repeatedly blocking and unblocking, invoking
//	 * {@link #tryAcquireShared} until success or the thread is interrupted.
//	 *
//	 * @param arg the acquire argument. This value is conveyed to
//	 *            {@link #tryAcquireShared} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @throws InterruptedException
//	 *             if the current thread is interrupted
//	 */
//	public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
//		if (Thread.interrupted()) {
//			throw new InterruptedException();
//		}
//		if (tryAcquireShared(arg) < 0) {
//			doAcquireSharedInterruptibly(arg);
//		}
//	}
//
//	/**
//	 * Attempts to acquire in shared mode, aborting if interrupted, and failing if
//	 * the given timeout elapses. Implemented by first checking interrupt status,
//	 * then invoking at least once {@link #tryAcquireShared}, returning on success.
//	 * Otherwise, the thread is queued, possibly repeatedly blocking and unblocking,
//	 * invoking {@link #tryAcquireShared} until success or the thread is interrupted
//	 * or the timeout elapses.
//	 *
//	 * @param arg the acquire argument. This value is conveyed to
//	 *            {@link #tryAcquireShared} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @param nanosTimeout
//	 *            the maximum number of nanoseconds to wait
//	 * @return {@code true} if acquired; {@code false} if timed out
//	 * @throws InterruptedException
//	 *             if the current thread is interrupted
//	 */
//	public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
//		if (Thread.interrupted()) {
//			throw new InterruptedException();
//		}
//
//		return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
//	}
//
//	/**
//	 * Releases in shared mode. Implemented by unblocking one or more threads if
//	 * {@link #tryReleaseShared} returns true.
//	 *
//	 * @param arg the release argument. This value is conveyed to
//	 *            {@link #tryReleaseShared} but is otherwise uninterpreted and can
//	 *            represent anything you like.
//	 * @return the value returned from {@link #tryReleaseShared}
//	 */
//	public final boolean releaseShared(int arg) {
//		if (tryReleaseShared(arg)) {
//			doReleaseShared();
//			return true;
//		}
//
//		return false;
//	}
//
//	// Queue inspection methods
//
//	/**
//	 * Queries whether any threads are waiting to acquire. Note that because
//	 * cancellations due to interrupts and timeouts may occur at any time, a
//	 * {@code true} return does not guarantee that any other thread will ever
//	 * acquire.
//	 * 查询是否有线程正在等待获取锁
//	 * 请注意，由于中断和超时导致的取消可能随时发生，因此返回true不能保证任何其他线程将获得锁
//	 *
//	 * <p>
//	 * In this implementation, this operation returns in constant time.
//	 * 在该实现中，操作以指定的时间返回
//	 *
//	 * @return {@code true} if there may be other threads waiting to acquire
//	 */
//	public final boolean hasQueuedThreads() {
//		return head != tail;
//	}
//
//	/**
//	 * Queries whether any threads have ever contended to acquire this synchronizer;
//	 * that is if an acquire method has ever blocked.
//	 * 查询是否有任何线程曾争用获取此同步器，也就是说，是否某个获取锁方法曾被阻塞
//	 *
//	 * <p>
//	 * In this implementation, this operation returns in constant time.
//	 * 在该实现中，操作以指定的时间返回
//	 *
//	 * @return {@code true} if there has ever been contention
//	 */
//	public final boolean hasContended() {
//		return head != null;
//	}
//
//	/**
//	 * Returns the first (longest-waiting) thread in the queue, or {@code null} if
//	 * no threads are currently queued.
//	 *
//	 * <p>
//	 * In this implementation, this operation normally returns in constant time, but
//	 * may iterate upon contention if other threads are concurrently modifying the
//	 * queue.
//	 *
//	 * @return the first (longest-waiting) thread in the queue, or {@code null} if
//	 *         no threads are currently queued
//	 */
//	public final Thread getFirstQueuedThread() {
//		// handle only fast path, else relay
//		return (head == tail) ? null : fullGetFirstQueuedThread();
//	}
//
//	/**
//	 * Version of getFirstQueuedThread called when fastpath fails
//	 */
//	private Thread fullGetFirstQueuedThread() {
//		/*
//		 * The first node is normally head.next. Try to get its thread field, ensuring
//		 * consistent reads: If thread field is nulled out or s.prev is no longer head,
//		 * then some other thread(s) concurrently performed setHead in between some of
//		 * our reads. We try this twice before resorting to traversal.
//		 */
//		Node h, s;
//		Thread st;
//		if (((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null)
//			|| ((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null)) {
//			return st;
//		}
//
//		/*
//		 * Head's next field might not have been set yet, or may have been unset after
//		 * setHead. So we must check to see if tail is actually first node. If not, we
//		 * continue on, safely traversing from tail back to head to find first,
//		 * guaranteeing termination.
//		 */
//
//		Node t = tail;
//		Thread firstThread = null;
//		while (t != null && t != head) {
//			Thread tt = t.thread;
//			if (tt != null) {
//				firstThread = tt;
//			}
//			t = t.prev;
//		}
//
//		return firstThread;
//	}
//
//	/**
//	 * Returns true if the given thread is currently queued.
//	 *
//	 * <p>
//	 * This implementation traverses the queue to determine presence of the given thread.
//	 *
//	 * @param thread the thread
//	 * @return {@code true} if the given thread is on the queue
//	 * @throws NullPointerException if the thread is null
//	 */
//	public final boolean isQueued(Thread thread) {
//		if (thread == null) {
//			throw new NullPointerException();
//		}
//		for (Node p = tail; p != null; p = p.prev) {
//			if (p.thread == thread) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	/**
//	 * Returns {@code true} if the apparent first queued thread, if one exists, is
//	 * waiting in exclusive mode. If this method returns {@code true}, and the
//	 * current thread is attempting to acquire in shared mode (that is, this method
//	 * is invoked from {@link #tryAcquireShared}) then it is guaranteed that the
//	 * current thread is not the first queued thread. Used only as a heuristic in
//	 * ReentrantReadWriteLock.
//	 */
//	final boolean apparentlyFirstQueuedIsExclusive() {
//		Node h, s;
//		return (h = head) != null && (s = h.next) != null && !s.isShared() && s.thread != null;
//	}
//
//	/**
//	 * Queries whether any threads have been waiting to acquire longer than the
//	 * current thread.
//	 * 查询是否有任何线程等待获取锁的时间超过当前线程
//	 *
//	 * <p>
//	 * An invocation of this method is equivalent to (but may be more efficient
//	 * than):
//	 * 调用此方法相当于调用：getFirstQueuedThread() != Thread.currentThread() && hasQueuedThreads()
//	 *
//	 * <pre>
//	 *  {@code getFirstQueuedThread() != Thread.currentThread() && hasQueuedThreads()}
//	 * </pre>
//	 *
//	 * <p>
//	 * Note that because cancellations due to interrupts and timeouts may occur at
//	 * any time, a {@code true} return does not guarantee that some other thread
//	 * will acquire before the current thread. Likewise, it is possible for another
//	 * thread to win a race to enqueue after this method has returned {@code false},
//	 * due to the queue being empty.
//	 *
//	 * <p>
//	 * This method is designed to be used by a fair synchronizer to avoid
//	 * <a href="AbstractQueuedSynchronizer#barging">barging</a>. Such a
//	 * synchronizer's {@link #tryAcquire} method should return {@code false}, and
//	 * its {@link #tryAcquireShared} method should return a negative value, if this
//	 * method returns {@code true} (unless this is a reentrant acquire). For
//	 * example, the {@codetryAcquire} method for a fair, reentrant, exclusive mode
//	 * synchronizer might look like this:
//	 *
//	 * <pre>
//	 *  {@code
//	 * protected boolean tryAcquire(int arg) {
//	 *   if (isHeldExclusively()) {
//	 *     // A reentrant acquire; increment hold count
//	 *     return true;
//	 *   } else if (hasQueuedPredecessors()) {
//	 *     return false;
//	 *   } else {
//	 *     // try to acquire normally
//	 *   }
//	 * }}
//	 * </pre>
//	 *
//	 * @return {@code true} if there is a queued thread preceding the current
//	 *         thread, and {@code false} if the current thread is at the head of the
//	 *         queue or the queue is empty
//	 * @since 1.7
//	 */
//	public final boolean hasQueuedPredecessors() {
//		// The correctness of this depends on head being initialized
//		// before tail and on head.next being accurate if the current
//		// thread is first in queue.
//		Node t = tail; // Read fields in reverse initialization order
//		Node h = head;
//		Node s;
//
//		return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
//	}
//
//	// Instrumentation and monitoring methods
//
//	/**
//	 * Returns an estimate of the number of threads waiting to acquire. The value is
//	 * only an estimate because the number of threads may change dynamically while
//	 * this method traverses internal data structures. This method is designed for
//	 * use in monitoring system state, not for synchronization control.
//	 *
//	 * @return the estimated number of threads waiting to acquire
//	 */
//	public final int getQueueLength() {
//		int n = 0;
//		for (Node p = tail; p != null; p = p.prev) {
//			if (p.thread != null) {
//				++n;
//			}
//		}
//
//		return n;
//	}
//
//	/**
//	 * Returns a collection containing threads that may be waiting to acquire.
//	 * Because the actual set of threads may change dynamically while constructing
//	 * this result, the returned collection is only a best-effort estimate. The
//	 * elements of the returned collection are in no particular order. This method
//	 * is designed to facilitate construction of subclasses that provide more
//	 * extensive monitoring facilities.
//	 *
//	 * @return the collection of threads
//	 */
//	public final Collection<Thread> getQueuedThreads() {
//		ArrayList<Thread> list = new ArrayList<Thread>();
//		for (Node p = tail; p != null; p = p.prev) {
//			Thread t = p.thread;
//			if (t != null) {
//				list.add(t);
//			}
//		}
//
//		return list;
//	}
//
//	/**
//	 * Returns a collection containing threads that may be waiting to acquire in
//	 * exclusive mode. This has the same properties as {@link #getQueuedThreads}
//	 * except that it only returns those threads waiting due to an exclusive
//	 * acquire.
//	 *
//	 * @return the collection of threads
//	 */
//	public final Collection<Thread> getExclusiveQueuedThreads() {
//		ArrayList<Thread> list = new ArrayList<Thread>();
//		for (Node p = tail; p != null; p = p.prev) {
//			if (!p.isShared()) {
//				Thread t = p.thread;
//				if (t != null) {
//					list.add(t);
//				}
//			}
//		}
//		return list;
//	}
//
//	/**
//	 * Returns a collection containing threads that may be waiting to acquire in
//	 * shared mode. This has the same properties as {@link #getQueuedThreads} except
//	 * that it only returns those threads waiting due to a shared acquire.
//	 *
//	 * @return the collection of threads
//	 */
//	public final Collection<Thread> getSharedQueuedThreads() {
//		ArrayList<Thread> list = new ArrayList<Thread>();
//		for (Node p = tail; p != null; p = p.prev) {
//			if (p.isShared()) {
//				Thread t = p.thread;
//				if (t != null) {
//					list.add(t);
//				}
//			}
//		}
//
//		return list;
//	}
//
//	/**
//	 * Returns a string identifying this synchronizer, as well as its state. The
//	 * state, in brackets, includes the String {@code "State ="} followed by the
//	 * current value of {@link #getState}, and either {@code "nonempty"} or
//	 * {@code "empty"} depending on whether the queue is empty.
//	 *
//	 * @return a string identifying this synchronizer, as well as its state
//	 */
//	public String toString() {
//		int s = getState();
//		String q = hasQueuedThreads() ? "non" : "";
//
//		return super.toString() + "[State = " + s + ", " + q + "empty queue]";
//	}
//
//	// Internal support methods for Conditions
//
//	/**
//	 * Returns true if a node, always one that was initially placed on a condition
//	 * queue, is now waiting to reacquire on sync queue.
//	 *
//	 * @param node the node
//	 * @return true if is reacquiring
//	 */
//	final boolean isOnSyncQueue(Node node) {
//		if (node.waitStatus == Node.CONDITION || node.prev == null) {
//			return false;
//		}
//		if (node.next != null) {// If has successor, it must be on queue
//			return true;
//		}
//
//		/*
//		 * node.prev can be non-null, but not yet on queue because the CAS to place it
//		 * on queue can fail. So we have to traverse from tail to make sure it actually
//		 * made it. It will always be near the tail in calls to this method, and unless
//		 * the CAS failed (which is unlikely), it will be there, so we hardly ever
//		 * traverse much.
//		 */
//		return findNodeFromTail(node);
//	}
//
//	/**
//	 * Returns true if node is on sync queue by searching backwards from tail.
//	 * Called only when needed by isOnSyncQueue.
//	 *
//	 * @return true if present
//	 */
//	private boolean findNodeFromTail(Node node) {
//		Node t = tail;
//		for (;;) {
//			if (t == node) {
//				return true;
//			}
//			if (t == null) {
//				return false;
//			}
//			t = t.prev;
//		}
//	}
//
//	/**
//	 * Transfers a node from a condition queue onto sync queue. Returns true if
//	 * successful.
//	 *
//	 * @param node the node
//	 * @return true if successfully transferred (else the node was cancelled before
//	 *         signal)
//	 */
//	final boolean transferForSignal(Node node) {
//		/*
//		 * If cannot change waitStatus, the node has been cancelled.
//		 */
//		if (!compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
//			return false;
//		}
//
//		/*
//		 * Splice onto queue and try to set waitStatus of predecessor to indicate that
//		 * thread is (probably) waiting. If cancelled or attempt to set waitStatus
//		 * fails, wake up to resync (in which case the waitStatus can be transiently and
//		 * harmlessly wrong).
//		 */
//		Node p = enq(node);
//		int ws = p.waitStatus;
//		if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL)) {
//			LockSupport.unpark(node.thread);
//		}
//
//		return true;
//	}
//
//	/**
//	 * Transfers node, if necessary, to sync queue after a cancelled wait. Returns
//	 * true if thread was cancelled before being signalled.
//	 *
//	 * @param node the node
//	 * @return true if cancelled before the node was signalled
//	 */
//	final boolean transferAfterCancelledWait(Node node) {
//		if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
//			enq(node);
//			return true;
//		}
//		/*
//		 * If we lost out to a signal(), then we can't proceed until it finishes its
//		 * enq(). Cancelling during an incomplete transfer is both rare and transient,
//		 * so just spin.
//		 */
//		while (!isOnSyncQueue(node)) {
//			Thread.yield();
//		}
//
//		return false;
//	}
//
//	/**
//	 * Invokes release with current state value; returns saved state. Cancels node
//	 * and throws exception on failure.
//	 *
//	 * @param node the condition node for this wait
//	 * @return previous sync state
//	 */
//	final int fullyRelease(Node node) {
//		boolean failed = true;
//		try {
//			int savedState = getState();
//			if (release(savedState)) {
//				failed = false;
//				return savedState;
//			} else {
//				throw new IllegalMonitorStateException();
//			}
//		} finally {
//			if (failed) {
//				node.waitStatus = Node.CANCELLED;
//			}
//		}
//	}
//
//	// Instrumentation methods for conditions
//
//	/**
//	 * Queries whether the given ConditionObject uses this synchronizer as its lock.
//	 *
//	 * @param condition the condition
//	 * @return {@code true} if owned
//	 * @throws NullPointerException if the condition is null
//	 */
//	public final boolean owns(ConditionObject condition) {
//		return condition.isOwnedBy(this);
//	}
//
//	/**
//	 * Queries whether any threads are waiting on the given condition associated
//	 * with this synchronizer. Note that because timeouts and interrupts may occur
//	 * at any time, a {@code true} return does not guarantee that a future
//	 * {@code signal} will awaken any threads. This method is designed primarily for
//	 * use in monitoring of the system state.
//	 *
//	 * @param condition the condition
//	 * @return {@code true} if there are any waiting threads
//	 * @throws IllegalMonitorStateException if exclusive synchronization is not held
//	 * @throws IllegalArgumentException
//	 *             if the given condition is not associated with this synchronizer
//	 * @throws NullPointerException if the condition is null
//	 */
//	public final boolean hasWaiters(ConditionObject condition) {
//		if (!owns(condition)) {
//			throw new IllegalArgumentException("Not owner");
//		}
//
//		return condition.hasWaiters();
//	}
//
//	/**
//	 * Returns an estimate of the number of threads waiting on the given condition
//	 * associated with this synchronizer. Note that because timeouts and interrupts
//	 * may occur at any time, the estimate serves only as an upper bound on the
//	 * actual number of waiters. This method is designed for use in monitoring of
//	 * the system state, not for synchronization control.
//	 *
//	 * @param condition the condition
//	 * @return the estimated number of waiting threads
//	 * @throws IllegalMonitorStateException if exclusive synchronization is not held
//	 * @throws IllegalArgumentException
//	 *             if the given condition is not associated with this synchronizer
//	 * @throws NullPointerException if the condition is null
//	 */
//	public final int getWaitQueueLength(ConditionObject condition) {
//		if (!owns(condition)) {
//			throw new IllegalArgumentException("Not owner");
//		}
//
//		return condition.getWaitQueueLength();
//	}
//
//	/**
//	 * Returns a collection containing those threads that may be waiting on the
//	 * given condition associated with this synchronizer. Because the actual set of
//	 * threads may change dynamically while constructing this result, the returned
//	 * collection is only a best-effort estimate. The elements of the returned
//	 * collection are in no particular order.
//	 *
//	 * @param condition the condition
//	 * @return the collection of threads
//	 * @throws IllegalMonitorStateException if exclusive synchronization is not held
//	 * @throws IllegalArgumentException
//	 *             if the given condition is not associated with this synchronizer
//	 * @throws NullPointerException if the condition is null
//	 */
//	public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
//		if (!owns(condition)) {
//			throw new IllegalArgumentException("Not owner");
//		}
//
//		return condition.getWaitingThreads();
//	}
//
//	/**
//	 * Condition implementation for a {@link AbstractQueuedSynchronizer} serving as
//	 * the basis of a {@link Lock} implementation.
//	 *
//	 * <p>
//	 * Method documentation for this class describes mechanics, not behavioral
//	 * specifications from the point of view of Lock and Condition users. Exported
//	 * versions of this class will in general need to be accompanied by
//	 * documentation describing condition semantics that rely on those of the
//	 * associated {@code AbstractQueuedSynchronizer}.
//	 *
//	 * <p>
//	 * This class is Serializable, but all fields are transient, so deserialized
//	 * conditions have no waiters.
//	 */
//	public class ConditionObject implements Condition, java.io.Serializable {
//		private static final long serialVersionUID = 1173984872572414699L;
//		/** First node of condition queue. */
//		private transient Node firstWaiter;
//		/** Last node of condition queue. */
//		private transient Node lastWaiter;
//
//		/**
//		 * Creates a new {@code ConditionObject} instance.
//		 */
//		public ConditionObject() {
//		}
//
//		// Internal methods
//
//		/**
//		 * Adds a new waiter to wait queue.
//		 *
//		 * @return its new wait node
//		 */
//		private Node addConditionWaiter() {
//			Node t = lastWaiter;
//			// If lastWaiter is cancelled, clean out.
//			if (t != null && t.waitStatus != Node.CONDITION) {
//				unlinkCancelledWaiters();
//				t = lastWaiter;
//			}
//			Node node = new Node(Thread.currentThread(), Node.CONDITION);
//			if (t == null) {
//				firstWaiter = node;
//			} else {
//				t.nextWaiter = node;
//			}
//			lastWaiter = node;
//
//			return node;
//		}
//
//		/**
//		 * Removes and transfers nodes until hit non-cancelled one or null. Split out
//		 * from signal in part to encourage compilers to inline the case of no waiters.
//		 *
//		 * @param first (non-null) the first node on condition queue
//		 */
//		private void doSignal(Node first) {
//			do {
//				if ((firstWaiter = first.nextWaiter) == null) {
//					lastWaiter = null;
//				}
//				first.nextWaiter = null;
//			} while (!transferForSignal(first) && (first = firstWaiter) != null);
//		}
//
//		/**
//		 * Removes and transfers all nodes.
//		 *
//		 * @param first (non-null) the first node on condition queue
//		 */
//		private void doSignalAll(Node first) {
//			lastWaiter = firstWaiter = null;
//			do {
//				Node next = first.nextWaiter;
//				first.nextWaiter = null;
//				transferForSignal(first);
//				first = next;
//			} while (first != null);
//		}
//
//		/**
//		 * Unlinks cancelled waiter nodes from condition queue. Called only while
//		 * holding lock. This is called when cancellation occurred during condition
//		 * wait, and upon insertion of a new waiter when lastWaiter is seen to have been
//		 * cancelled. This method is needed to avoid garbage retention in the absence of
//		 * signals. So even though it may require a full traversal, it comes into play
//		 * only when timeouts or cancellations occur in the absence of signals. It
//		 * traverses all nodes rather than stopping at a particular target to unlink all
//		 * pointers to garbage nodes without requiring many re-traversals during
//		 * cancellation storms.
//		 */
//		private void unlinkCancelledWaiters() {
//			Node t = firstWaiter;
//			Node trail = null;
//			while (t != null) {
//				Node next = t.nextWaiter;
//				if (t.waitStatus != Node.CONDITION) {
//					t.nextWaiter = null;
//					if (trail == null) {
//						firstWaiter = next;
//					} else {
//						trail.nextWaiter = next;
//					}
//					if (next == null) {
//						lastWaiter = trail;
//					}
//				} else {
//					trail = t;
//				}
//				t = next;
//			}
//		}
//
//		// public methods
//
//		/**
//		 * Moves the longest-waiting thread, if one exists, from the wait queue for this
//		 * condition to the wait queue for the owning lock.
//		 *
//		 * @throws IllegalMonitorStateException
//		 *             if {@link #isHeldExclusively} returns {@code false}
//		 */
//		public final void signal() {
//			if (!isHeldExclusively()) {
//				throw new IllegalMonitorStateException();
//			}
//			Node first = firstWaiter;
//			if (first != null) {
//				doSignal(first);
//			}
//		}
//
//		/**
//		 * Moves all threads from the wait queue for this condition to the wait queue
//		 * for the owning lock.
//		 *
//		 * @throws IllegalMonitorStateException
//		 *             if {@link #isHeldExclusively} returns {@code false}
//		 */
//		public final void signalAll() {
//			if (!isHeldExclusively()) {
//				throw new IllegalMonitorStateException();
//			}
//			Node first = firstWaiter;
//			if (first != null) {
//				doSignalAll(first);
//			}
//		}
//
//		/**
//		 * Implements uninterruptible condition wait.
//		 * 实现不可中断的condition等待
//		 *
//		 * <ol>
//		 * <li>Save lock state returned by {@link #getState}.
//		 * <li>Invoke {@link #release} with saved state as argument, throwing
//		 * IllegalMonitorStateException if it fails.
//		 * <li>Block until signalled.
//		 * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
//		 * state as argument.
//		 * </ol>
//		 */
//		public final void awaitUninterruptibly() {
//			Node node = addConditionWaiter();
//			int savedState = fullyRelease(node);
//			boolean interrupted = false;
//			while (!isOnSyncQueue(node)) {
//				LockSupport.park(this);
//				if (Thread.interrupted()) {
//					interrupted = true;
//				}
//			}
//			if (acquireQueued(node, savedState) || interrupted) {
//				selfInterrupt();
//			}
//		}
//
//		/*
//		 * For interruptible waits, we need to track whether to throw
//		 * InterruptedException, if interrupted while blocked on condition, versus
//		 * reinterrupt current thread, if interrupted while blocked waiting to
//		 * re-acquire.
//		 */
//
//		/** Mode meaning to reinterrupt on exit from wait */
//		private static final int REINTERRUPT = 1;
//		/** Mode meaning to throw InterruptedException on exit from wait */
//		private static final int THROW_IE = -1;
//
//		/**
//		 * Checks for interrupt, returning THROW_IE if interrupted before signalled,
//		 * REINTERRUPT if after signalled, or 0 if not interrupted.
//		 */
//		private int checkInterruptWhileWaiting(Node node) {
//			return Thread.interrupted() ? (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) : 0;
//		}
//
//		/**
//		 * Throws InterruptedException, reinterrupts current thread, or does nothing,
//		 * depending on mode.
//		 */
//		private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
//			if (interruptMode == THROW_IE) {
//				throw new InterruptedException();
//			} else if (interruptMode == REINTERRUPT) {
//				selfInterrupt();
//			}
//		}
//
//		/**
//		 * Implements interruptible condition wait.
//		 * 实现可中断的condition等待
//		 *
//		 * <ol>
//		 * <li>If current thread is interrupted, throw InterruptedException.
//		 * <li>Save lock state returned by {@link #getState}.
//		 * <li>Invoke {@link #release} with saved state as argument, throwing
//		 * IllegalMonitorStateException if it fails.
//		 * <li>Block until signalled or interrupted.
//		 * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
//		 * state as argument.
//		 * <li>If interrupted while blocked in step 4, throw InterruptedException.
//		 * </ol>
//		 */
//		public final void await() throws InterruptedException {
//			if (Thread.interrupted()) {
//				throw new InterruptedException();
//			}
//			Node node = addConditionWaiter();
//			int savedState = fullyRelease(node);
//			int interruptMode = 0;
//			while (!isOnSyncQueue(node)) {
//				LockSupport.park(this);
//				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
//					break;
//				}
//			}
//			if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
//				interruptMode = REINTERRUPT;
//			}
//			if (node.nextWaiter != null) {// clean up if cancelled
//				unlinkCancelledWaiters();
//			}
//			if (interruptMode != 0) {
//				reportInterruptAfterWait(interruptMode);
//			}
//		}
//
//		/**
//		 * Implements timed condition wait.
//		 * 实现超时condition等待
//		 *
//		 * <ol>
//		 * <li>If current thread is interrupted, throw InterruptedException.
//		 * <li>Save lock state returned by {@link #getState}.
//		 * <li>Invoke {@link #release} with saved state as argument, throwing
//		 * IllegalMonitorStateException if it fails.
//		 * <li>Block until signalled, interrupted, or timed out.
//		 * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
//		 * state as argument.
//		 * <li>If interrupted while blocked in step 4, throw InterruptedException.
//		 * </ol>
//		 */
//		public final long awaitNanos(long nanosTimeout) throws InterruptedException {
//			if (Thread.interrupted()) {
//				throw new InterruptedException();
//			}
//			Node node = addConditionWaiter();
//			int savedState = fullyRelease(node);
//			final long deadline = System.nanoTime() + nanosTimeout;
//			int interruptMode = 0;
//			while (!isOnSyncQueue(node)) {
//				if (nanosTimeout <= 0L) {
//					transferAfterCancelledWait(node);
//					break;
//				}
//				if (nanosTimeout >= spinForTimeoutThreshold) {
//					LockSupport.parkNanos(this, nanosTimeout);
//				}
//				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
//					break;
//				}
//				nanosTimeout = deadline - System.nanoTime();
//			}
//			if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
//				interruptMode = REINTERRUPT;
//			}
//			if (node.nextWaiter != null) {
//				unlinkCancelledWaiters();
//			}
//			if (interruptMode != 0) {
//				reportInterruptAfterWait(interruptMode);
//			}
//			return deadline - System.nanoTime();
//		}
//
//		/**
//		 * Implements absolute timed condition wait.
//		 * 实现绝对的超时condition等待
//		 *
//		 * <ol>
//		 * <li>If current thread is interrupted, throw InterruptedException.
//		 * <li>Save lock state returned by {@link #getState}.
//		 * <li>Invoke {@link #release} with saved state as argument, throwing
//		 * IllegalMonitorStateException if it fails.
//		 * <li>Block until signalled, interrupted, or timed out.
//		 * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
//		 * state as argument.
//		 * <li>If interrupted while blocked in step 4, throw InterruptedException.
//		 * <li>If timed out while blocked in step 4, return false, else true.
//		 * </ol>
//		 */
//		public final boolean awaitUntil(Date deadline) throws InterruptedException {
//			long abstime = deadline.getTime();
//			if (Thread.interrupted()) {
//				throw new InterruptedException();
//			}
//			Node node = addConditionWaiter();
//			int savedState = fullyRelease(node);
//			boolean timedout = false;
//			int interruptMode = 0;
//			while (!isOnSyncQueue(node)) {
//				if (System.currentTimeMillis() > abstime) {
//					timedout = transferAfterCancelledWait(node);
//					break;
//				}
//				LockSupport.parkUntil(this, abstime);
//				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
//					break;
//				}
//			}
//			if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
//				interruptMode = REINTERRUPT;
//			}
//			if (node.nextWaiter != null) {
//				unlinkCancelledWaiters();
//			}
//			if (interruptMode != 0) {
//				reportInterruptAfterWait(interruptMode);
//			}
//			return !timedout;
//		}
//
//		/**
//		 * Implements timed condition wait.
//		 * 实现超时condition等待
//		 *
//		 * <ol>
//		 * <li>If current thread is interrupted, throw InterruptedException.
//		 * <li>Save lock state returned by {@link #getState}.
//		 * <li>Invoke {@link #release} with saved state as argument, throwing
//		 * IllegalMonitorStateException if it fails.
//		 * <li>Block until signalled, interrupted, or timed out.
//		 * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
//		 * state as argument.
//		 * <li>If interrupted while blocked in step 4, throw InterruptedException.
//		 * <li>If timed out while blocked in step 4, return false, else true.
//		 * </ol>
//		 */
//		public final boolean await(long time, TimeUnit unit) throws InterruptedException {
//			long nanosTimeout = unit.toNanos(time);
//			if (Thread.interrupted()) {
//				throw new InterruptedException();
//			}
//			Node node = addConditionWaiter();
//			int savedState = fullyRelease(node);
//			final long deadline = System.nanoTime() + nanosTimeout;
//			boolean timedout = false;
//			int interruptMode = 0;
//			while (!isOnSyncQueue(node)) {
//				if (nanosTimeout <= 0L) {
//					timedout = transferAfterCancelledWait(node);
//					break;
//				}
//				if (nanosTimeout >= spinForTimeoutThreshold) {
//					LockSupport.parkNanos(this, nanosTimeout);
//				}
//				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
//					break;
//				}
//				nanosTimeout = deadline - System.nanoTime();
//			}
//			if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
//				interruptMode = REINTERRUPT;
//			}
//			if (node.nextWaiter != null) {
//				unlinkCancelledWaiters();
//			}
//			if (interruptMode != 0) {
//				reportInterruptAfterWait(interruptMode);
//			}
//			return !timedout;
//		}
//
//		// support for instrumentation
//
//		/**
//		 * Returns true if this condition was created by the given synchronization
//		 * object.
//		 *
//		 * @return {@code true} if owned
//		 */
//		final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
//			return sync == AbstractQueuedSynchronizer.this;
//		}
//
//		/**
//		 * Queries whether any threads are waiting on this condition. Implements
//		 * {@link AbstractQueuedSynchronizer#hasWaiters(ConditionObject)}.
//		 *
//		 * @return {@code true} if there are any waiting threads
//		 * @throws IllegalMonitorStateException
//		 *             if {@link #isHeldExclusively} returns {@code false}
//		 */
//		protected final boolean hasWaiters() {
//			if (!isHeldExclusively()) {
//				throw new IllegalMonitorStateException();
//			}
//			for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
//				if (w.waitStatus == Node.CONDITION) {
//					return true;
//				}
//			}
//
//			return false;
//		}
//
//		/**
//		 * Returns an estimate of the number of threads waiting on this condition.
//		 * Implements {@link AbstractQueuedSynchronizer#getWaitQueueLength(ConditionObject)}.
//		 *
//		 * @return the estimated number of waiting threads
//		 * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns {@code false}
//		 */
//		protected final int getWaitQueueLength() {
//			if (!isHeldExclusively()) {
//				throw new IllegalMonitorStateException();
//			}
//			int n = 0;
//			for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
//				if (w.waitStatus == Node.CONDITION) {
//					++n;
//				}
//			}
//
//			return n;
//		}
//
//		/**
//		 * Returns a collection containing those threads that may be waiting on this
//		 * Condition. Implements {@link AbstractQueuedSynchronizer#getWaitingThreads(ConditionObject)}.
//		 *
//		 * @return the collection of threads
//		 * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns {@code false}
//		 */
//		protected final Collection<Thread> getWaitingThreads() {
//			if (!isHeldExclusively()) {
//				throw new IllegalMonitorStateException();
//			}
//			ArrayList<Thread> list = new ArrayList<Thread>();
//			for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
//				if (w.waitStatus == Node.CONDITION) {
//					Thread t = w.thread;
//					if (t != null) {
//						list.add(t);
//					}
//				}
//			}
//
//			return list;
//		}
//	}
//
//	/**
//	 * Setup to support compareAndSet. We need to natively implement this here: For
//	 * the sake of permitting future enhancements, we cannot explicitly subclass
//	 * AtomicInteger, which would be efficient and useful otherwise. So, as the
//	 * lesser of evils, we natively implement using hotspot intrinsics API. And
//	 * while we are at it, we do the same for other CASable fields (which could
//	 * otherwise be done with atomic field updaters).
//	 */
//	private static final Unsafe unsafe = Unsafe.getUnsafe();
//	private static final long stateOffset;
//	private static final long headOffset;
//	private static final long tailOffset;
//	private static final long waitStatusOffset;
//	private static final long nextOffset;
//
//	static {
//		try {
//			stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
//			headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
//			tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
//			waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
//			nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
//
//		} catch (Exception ex) {
//			throw new Error(ex);
//		}
//	}
//
//	/**
//	 * CAS head field. Used only by enq.
//	 */
//	private final boolean compareAndSetHead(Node update) {
//		return unsafe.compareAndSwapObject(this, headOffset, null, update);
//	}
//
//	/**
//	 * CAS tail field. Used only by enq.
//	 */
//	private final boolean compareAndSetTail(Node expect, Node update) {
//		return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
//	}
//
//	/**
//	 * CAS waitStatus field of a node.
//	 */
//	private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
//		return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
//	}
//
//	/**
//	 * CAS next field of a node.
//	 */
//	private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
//		return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
//	}
//}
