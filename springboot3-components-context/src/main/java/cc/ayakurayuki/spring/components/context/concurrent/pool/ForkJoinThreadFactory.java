package cc.ayakurayuki.spring.components.context.concurrent.pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;

public class ForkJoinThreadFactory implements ForkJoinWorkerThreadFactory {

  private final String     name;
  private final AtomicLong count = new AtomicLong();

  public ForkJoinThreadFactory(String name) {
    this.name = name;
  }

  public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
    return new NamedForkJoinWorkerThread(String.format("%s-%d", name, count.incrementAndGet()), pool);
  }

}
