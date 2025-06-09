package cc.ayakurayuki.spring.components.context.concurrent.pool;

import java.util.concurrent.ForkJoinPool;

public class ForkJoin extends ForkJoinPool {

  public ForkJoin(int parallelism, ForkJoinWorkerThreadFactory factory) {
    this(parallelism, factory, null, true);
  }

  public ForkJoin(int parallelism, ForkJoinWorkerThreadFactory factory, Thread.UncaughtExceptionHandler handler, boolean asyncMode) {
    super(parallelism, factory, handler, asyncMode);
  }

  @Override
  public void execute(Runnable command) {
    super.execute(CommandWrapper.wrap(command));
  }

}
