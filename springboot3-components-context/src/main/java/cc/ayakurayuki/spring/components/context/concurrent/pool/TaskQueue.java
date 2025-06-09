package cc.ayakurayuki.spring.components.context.concurrent.pool;

import jakarta.annotation.Nonnull;
import java.io.Serial;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * As task queue specifically designed to run with a thread pool executor. The
 * task queue is optimized to properly utilize threads within a thread pool
 * executor. If you use a normal queue, the executor will spawn threads when
 * there are idle threads, and you won't be able to force items onto the queue
 * itself.
 */
class TaskQueue extends LinkedBlockingQueue<Runnable> {

  @Serial
  private static final long serialVersionUID = -1930693771858182898L;

  private transient volatile Executor parent = null;

  public TaskQueue() {
    super();
  }

  public void setParent(Executor parent) {
    this.parent = parent;
  }

  public boolean force(Runnable o) {
    if (parent == null || parent.isShutdown()) {
      return false;
    }
    return super.offer(o); // forces the item onto the queue, to be used if the task is rejected
  }

  @Override
  public boolean offer(@Nonnull Runnable o) {
    // we can't do any checks
    if (parent == null) {
      return super.offer(o);
    }
    // we are maxed out on threads, queue the object
    if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
      return super.offer(o);
    }
    // we have idle threads, add it to the queue
    if (parent.getSubmittedCount() <= parent.getPoolSize()) {
      return super.offer(o);
    }
    // if we have fewer threads than maximum force creation of a new thread
    if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
      return false;
    }
    // if we reached here, we need to add it to the queue
    return super.offer(o);
  }

}
