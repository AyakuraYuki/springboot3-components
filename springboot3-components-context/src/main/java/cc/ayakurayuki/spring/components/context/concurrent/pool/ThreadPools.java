package cc.ayakurayuki.spring.components.context.concurrent.pool;

import cc.ayakurayuki.spring.components.stats.model.ThreadPool;
import cc.ayakurayuki.spring.components.stats.stats.ScheduledStats;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.Nonnull;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Tips:
 *
 * <p>Way to override by custom ForkJoinPool:
 * <ol>
 *   <li>
 *     <ul>
 *       <li>key: {@code java.util.concurrent.ForkJoinPool.common.parallelism}</li>
 *       <li>value: {@code <DEFAULT_PARALLELISM>}</li>
 *       <li>DEFAULT_PARALLELISM = {@code (availableProcessors / (1 - blockingCoefficient)) * 2}</li>
 *     </ul>
 *   </li>
 *   <li>
 *     <ul>
 *       <li>key: {@code java.util.concurrent.ForkJoinPool.common.threadFactory}</li>
 *       <li>value: {@code cc.ayakurayuki.spring.components.context.concurrent.pool.ForkJoinThreadFactory}</li>
 *       <li>Reference: {@link cc.ayakurayuki.spring.components.context.concurrent.pool.ForkJoinThreadFactory}</li>
 *     </ul>
 *   </li>
 *   <li>
 *     <ul>
 *       <li>key: {@code java.util.concurrent.ForkJoinPool.common.maximumSpares}</li>
 *       <li>value: {@code <parallelism> like 256}</li>
 *       <li>this for tuning the maximum number of allowed extra threads to maintain target parallelism</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>And there are some ways to set those properties:
 * <ul>
 *   <li>Startup command with {@code -Dkey=value}</li>
 *   <li>{@link System#setProperty(String, String)} in main function.</li>
 *   <li>Spring Boot {@code @PostConstruct} function to {@link System#setProperty(String, String)}</li>
 *   <li>Spring Boot {@code spring.config.additional-location=classpath:system.properties} with {@code system.properties} configure file</li>
 *   <li>Use Java Agent</li>
 * </ul>
 */
public class ThreadPools {

  /**
   * the default blocking coefficient
   * <p>
   * 默认阻塞系数
   */
  private static final double DEFAULT_BLOCKING_COEFFICIENT = 0.9;

  /**
   * the default parallelism ((available_cores / (1 - blocking_coefficient)) * 2)
   * <p>
   * 默认线程池并发度 核数 / (1 - 阻塞系数) * 2
   */
  private static final int DEFAULT_PARALLELISM = (int) (Runtime.getRuntime().availableProcessors() / (1 - DEFAULT_BLOCKING_COEFFICIENT)) * 2;

  /**
   * customized ForkJoinPool
   */
  public static final ForkJoinPool common = newForkJoinPool("Fork-Join-Common-Pool", DEFAULT_PARALLELISM);

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name Thread prefix name
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name) {
    ThreadFactory factory = Thread.ofVirtual().name(name).factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name  Thread prefix name
   * @param start Start number of thread namings
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name, long start) {
    ThreadFactory factory = Thread.ofVirtual().name(name, start).factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name    Thread prefix name
   * @param inherit inherit {@link InheritableThreadLocal} from parent ({@code true}) or not ({@code false})
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name, boolean inherit) {
    ThreadFactory factory = Thread.ofVirtual()
        .name(name)
        .inheritInheritableThreadLocals(inherit)
        .factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name    Thread prefix name
   * @param start   Start number of thread namings
   * @param inherit inherit {@link InheritableThreadLocal} from parent ({@code true}) or not ({@code false})
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name, long start, boolean inherit) {
    ThreadFactory factory = Thread.ofVirtual()
        .name(name, start)
        .inheritInheritableThreadLocals(inherit)
        .factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name                     Thread prefix name
   * @param uncaughtExceptionHandler customized exception handler
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name, @Nonnull UncaughtExceptionHandler uncaughtExceptionHandler) {
    ThreadFactory factory = Thread.ofVirtual()
        .name(name)
        .uncaughtExceptionHandler(uncaughtExceptionHandler)
        .factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name                     Thread prefix name
   * @param start                    Start number of thread namings
   * @param uncaughtExceptionHandler customized exception handler
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name, long start, @Nonnull UncaughtExceptionHandler uncaughtExceptionHandler) {
    ThreadFactory factory = Thread.ofVirtual()
        .name(name, start)
        .uncaughtExceptionHandler(uncaughtExceptionHandler)
        .factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Directly create a new virtual thread executor by given name.
   *
   * @param name                     Thread prefix name
   * @param start                    Start number of thread namings
   * @param inherit                  inherit {@link InheritableThreadLocal} from parent ({@code true}) or not ({@code false})
   * @param uncaughtExceptionHandler customized exception handler
   */
  public static java.util.concurrent.Executor newVirtualExecutor(String name, long start, boolean inherit, @Nonnull UncaughtExceptionHandler uncaughtExceptionHandler) {
    ThreadFactory factory = Thread.ofVirtual()
        .name(name, start)
        .inheritInheritableThreadLocals(inherit)
        .uncaughtExceptionHandler(uncaughtExceptionHandler)
        .factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }

  /**
   * Create a new scalable thread pool executor with 5 mins keepAliveTime
   * <p>
   * Scalable thread pool similar to {@link ThreadPoolExecutor}, but have some difference:
   * <p>
   * 1. If the current thread pool is idle, all new tasks will be queued and execute
   * <p>
   * 2. If current thread pool size is less than maximumPoolSize and equal or more that corePoolSize, if new task enqueue to pool, current thread pool size will increase and at most equal to
   * maximumPoolSize.
   * <p>
   * 3. If the current thread pool size is equal to maximumPoolSize, all new tasks will queue
   *
   * @return executor
   */
  public static ThreadPoolExecutor newScalableThreadPool(String name, int corePoolSize, int maximumPoolSize) {
    return newScalableThreadPool(name, corePoolSize, maximumPoolSize, 5, TimeUnit.MINUTES);
  }

  /**
   * Create a new scalable thread pool executor
   * <p>
   * Scalable thread pool similar to {@link ThreadPoolExecutor}, but have some difference:
   * <p>
   * 1. If the current thread pool is idle, all new tasks will be queued and execute
   * <p>
   * 2. If current thread pool size is less than maximumPoolSize and equal or more that corePoolSize, if the new task enqueue to pool, current thread pool size will increase and at most equal to
   * maximumPoolSize.
   * <p>
   * 3. If the current thread pool size is equal to maximumPoolSize, all new tasks will queue
   *
   * @return executor
   */
  public static ThreadPoolExecutor newScalableThreadPool(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
    return newScalableThreadPool(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, true);
  }

  /**
   * Create a new scalable thread pool executor
   * <p>
   * Scalable thread pool similar to {@link ThreadPoolExecutor}, but have some difference:
   * <p>
   * 1. If the current thread pool is idle, all new tasks will be queued and execute
   * <p>
   * 2. If current thread pool size is less than maximumPoolSize and equal or more that corePoolSize, if the new task enqueue to pool, current thread pool size will increase and at most equal to
   * maximumPoolSize.
   * <p>
   * 3. If the current thread pool size is equal to maximumPoolSize, all new tasks will be queued
   *
   * @return executor
   */
  public static ThreadPoolExecutor newScalableThreadPool(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, boolean daemon) {
    return newScalableThreadPool(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, Executor.DEFAULT_HANDLER, daemon);
  }

  /**
   * Create a new scalable thread pool executor
   * <p>
   * Scalable thread pool similar to {@link ThreadPoolExecutor}, but have some difference:
   * <p>
   * 1. If the current thread pool is idle, all new tasks will be queued and execute
   * <p>
   * 2. If current thread pool size is less than maximumPoolSize and equal or more that corePoolSize, if the new task enqueue to pool, current thread pool size will increase and at most equal to
   * maximumPoolSize.
   * <p>
   * 3. If the current thread pool size is equal to maximumPoolSize, all new tasks will be queued
   *
   * @return executor
   */
  public static ThreadPoolExecutor newScalableThreadPool(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler) {
    return newScalableThreadPool(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, handler, true);
  }

  /**
   * Create a new scalable thread pool executor
   * <p>
   * Scalable thread pool similar to {@link ThreadPoolExecutor}, but have some difference:
   * <p>
   * 1. If the current thread pool is idle, all new tasks will be queued and execute
   * <p>
   * 2. If current thread pool size is less than maximumPoolSize and equal or more that corePoolSize, if new task enqueue to pool, current thread pool size will increase and at most equal to
   * maximumPoolSize.
   * <p>
   * 3. If the current thread pool size is equal to maximumPoolSize, all new tasks will be queued
   *
   * @return executor
   */
  public static ThreadPoolExecutor newScalableThreadPool(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler, boolean daemon) {
    TaskQueue queue = new TaskQueue();
    Executor executor = new Executor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, getFactory(name, daemon), handler);
    queue.setParent(executor);
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create new thread poll executor
   *
   * @return executor
   */
  public static ThreadPoolExecutor newThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    return newThreadPoolExecutor(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, true);
  }

  /**
   * Create new thread poll executor
   *
   * @return executor
   */
  public static ThreadPoolExecutor newThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, boolean daemon) {
    ThreadPoolExecutor executor = new Executor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, getFactory(name, daemon));
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create new thread poll executor
   *
   * @return executor
   */
  public static ThreadPoolExecutor newThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                         RejectedExecutionHandler handler) {
    return newThreadPoolExecutor(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler, true);
  }

  /**
   * Create new thread poll executor
   *
   * @return executor
   */
  public static ThreadPoolExecutor newThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                         RejectedExecutionHandler handler, boolean daemon) {
    ThreadPoolExecutor executor = new Executor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, getFactory(name, daemon), handler);
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create a new scheduled thread poll executor
   *
   * @return executor
   *
   * @see Executors#newScheduledThreadPool(int)
   */
  public static ScheduledThreadPoolExecutor newScheduledThreadPool(String name, int corePoolSize) {
    return newScheduledThreadPool(name, corePoolSize, true);
  }

  /**
   * Create a new scheduled thread poll executor
   *
   * @return executor
   *
   * @see Executors#newScheduledThreadPool(int, ThreadFactory)
   */
  public static ScheduledThreadPoolExecutor newScheduledThreadPool(String name, int corePoolSize, boolean daemon) {
    ScheduledExecutor executor = new ScheduledExecutor(corePoolSize, getFactory(name, daemon));
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create new scheduled thread poll executor
   *
   * @return executor
   *
   * @see Executors#newScheduledThreadPool(int)
   */
  public static ScheduledThreadPoolExecutor newScheduledThreadPool(String name, int corePoolSize, RejectedExecutionHandler handler) {
    return newScheduledThreadPool(name, corePoolSize, handler, true);
  }

  /**
   * Create a new scheduled thread poll executor
   *
   * @return executor
   *
   * @see Executors#newScheduledThreadPool(int, ThreadFactory)
   */
  public static ScheduledThreadPoolExecutor newScheduledThreadPool(String name, int corePoolSize, RejectedExecutionHandler handler, boolean daemon) {
    ScheduledExecutor executor = new ScheduledExecutor(corePoolSize, getFactory(name, daemon), handler);
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create a new cached thread poll executor
   *
   * @return executor
   *
   * @see Executors#newCachedThreadPool()
   */
  public static ThreadPoolExecutor newCachedThreadPool(String name) {
    return newCachedThreadPool(name, true);
  }

  /**
   * Create a new cached thread poll executor
   *
   * @return executor
   *
   * @see Executors#newCachedThreadPool(ThreadFactory)
   */
  public static ThreadPoolExecutor newCachedThreadPool(String name, boolean daemon) {
    ThreadPoolExecutor executor = new Executor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), getFactory(name, daemon));
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create a new fixed thread poll executor
   *
   * @return executor
   *
   * @see Executors#newFixedThreadPool(int)
   */
  public static ThreadPoolExecutor newFixedThreadPool(String name, int nThreads) {
    return newFixedThreadPool(name, nThreads, true);
  }

  /**
   * Create a new fixed thread poll executor
   *
   * @return executor
   *
   * @see Executors#newFixedThreadPool(int, ThreadFactory)
   */
  public static ThreadPoolExecutor newFixedThreadPool(String name, int nThreads, boolean daemon) {
    ThreadPoolExecutor executor = new Executor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), getFactory(name, daemon));
    attachMonitor(name, executor);
    return executor;
  }

  /**
   * Create a new work stealing pool
   *
   * @return executor
   *
   * @see Executors#newWorkStealingPool(int)
   */
  public static ForkJoinPool newForkJoinPool(String name, int parallelism) {
    ForkJoin forkJoin = new ForkJoin(parallelism, getForkJoinFactory(name));
    attachMonitor(name, forkJoin);
    return forkJoin;
  }

  /**
   * Create a new work stealing pool
   *
   * @return executor
   *
   * @see Executors#newWorkStealingPool(int)
   */
  public static ForkJoinPool newForkJoinPool(String name, int parallelism, Thread.UncaughtExceptionHandler handler, boolean asyncMode) {
    ForkJoin forkJoin = new ForkJoin(parallelism, getForkJoinFactory(name), handler, asyncMode);
    attachMonitor(name, forkJoin);
    return forkJoin;
  }

  private static ThreadFactory getFactory(String name, boolean daemon) {
    return new ThreadFactoryBuilder()
        .setNameFormat(name + "-%d")
        .setDaemon(daemon)
        .build();
  }

  private static ForkJoinThreadFactory getForkJoinFactory(String name) {
    return new ForkJoinThreadFactory(name);
  }

  private static void attachMonitor(String name, AbstractExecutorService executor) {
    WeakReference<AbstractExecutorService> executorReference = new WeakReference<>(executor);
    ScheduledStats.scheduleWithFixedDelay(
        () -> {
          AbstractExecutorService currentExecutor = executorReference.get();
          if (currentExecutor == null) {
            throw new IllegalArgumentException("Current executor reference is null, cancel the task");
          }
          if (currentExecutor.isShutdown()) {
            throw new IllegalArgumentException("Current executor reference is shutdown, cancel the task");
          }
          if (currentExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor t = (ThreadPoolExecutor) currentExecutor;
            ThreadPool.POOL_STATE_ACTIVE.set(t.getActiveCount(), name); // 返回正在积极执行任务的线程的大致数目
            if (!(currentExecutor instanceof ScheduledThreadPoolExecutor)) {
              ThreadPool.POOL_STATE_TASK_WAITING.set(t.getQueue().size(), name); // 提交到此池中，但未开始执行的任务数量
            }
          }
          if (currentExecutor instanceof ForkJoinPool) {
            ForkJoinPool f = (ForkJoinPool) currentExecutor;
            ThreadPool.POOL_STATE_ACTIVE.set(f.getActiveThreadCount(), name); // 正在窃取或运行中的线程数
            ThreadPool.POOL_STATE_TASK_WAITING.set(f.getQueuedSubmissionCount(), name); // 提交到此池中，但未开始执行的任务数量
          }
        },
        1, 1, TimeUnit.SECONDS
    );
  }

}
