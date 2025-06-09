package cc.ayakurayuki.spring.components.context.concurrent.pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class NamedForkJoinWorkerThread extends ForkJoinWorkerThread {

  protected NamedForkJoinWorkerThread(String name, ForkJoinPool pool) {
    super(pool);
    super.setName(name);
    super.setContextClassLoader(ClassLoader.getSystemClassLoader());
  }

}
