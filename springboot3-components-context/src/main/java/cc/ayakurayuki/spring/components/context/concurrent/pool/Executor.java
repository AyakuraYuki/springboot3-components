package cc.ayakurayuki.spring.components.context.concurrent.pool;

import jakarta.annotation.Nonnull;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class Executor extends ThreadPoolExecutor {

  static final RejectedExecutionHandler DEFAULT_HANDLER = new AbortPolicy();

  private final AtomicInteger submittedCount = new AtomicInteger(0);

  public Executor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
    this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, DEFAULT_HANDLER);
  }

  public Executor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    setRejectedExecutionHandler(handler);
  }

  @Override
  public void execute(@Nonnull Runnable command) {
    submittedCount.incrementAndGet();
    var wrap = CommandWrapper.wrap(command);
    super.execute(wrap);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    submittedCount.decrementAndGet();
  }

  @Override
  public void setRejectedExecutionHandler(@Nonnull RejectedExecutionHandler handler) {
    super.setRejectedExecutionHandler((task, executor) -> {
      BlockingQueue<Runnable> queue = getQueue();
      if (queue instanceof TaskQueue taskQueue) {
        if (!taskQueue.force(task)) {
          submittedCount.decrementAndGet();
          handler.rejectedExecution(task, executor);
        }
      } else {
        submittedCount.decrementAndGet();
        handler.rejectedExecution(task, executor);
      }
    });
  }

  public int getSubmittedCount() {
    return submittedCount.get();
  }

}
