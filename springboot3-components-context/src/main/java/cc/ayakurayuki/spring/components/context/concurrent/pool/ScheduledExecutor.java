package cc.ayakurayuki.spring.components.context.concurrent.pool;

import jakarta.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class ScheduledExecutor extends ScheduledThreadPoolExecutor {

  public ScheduledExecutor(int corePoolSize, ThreadFactory threadFactory) {
    super(corePoolSize, threadFactory);
  }

  public ScheduledExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
    super(corePoolSize, threadFactory, handler);
  }

  @Override
  public void execute(@Nonnull Runnable command) {
    super.execute(CommandWrapper.wrap(command));
  }

  @Nonnull
  @Override
  public ScheduledFuture<?> schedule(@Nonnull Runnable command, long delay, @Nonnull TimeUnit unit) {
    return super.schedule(CommandWrapper.wrap(command), delay, unit);
  }

  @Nonnull
  @Override
  public <V> ScheduledFuture<V> schedule(@Nonnull Callable<V> callable, long delay, @Nonnull TimeUnit unit) {
    return super.schedule(CommandWrapper.wrap(callable), delay, unit);
  }

  @Nonnull
  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(@Nonnull Runnable command, long initialDelay, long period, @Nonnull TimeUnit unit) {
    return super.scheduleAtFixedRate(CommandWrapper.wrap(command), initialDelay, period, unit);
  }

  @Nonnull
  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(@Nonnull Runnable command, long initialDelay, long delay, @Nonnull TimeUnit unit) {
    return super.scheduleWithFixedDelay(CommandWrapper.wrap(command), initialDelay, delay, unit);
  }

}
