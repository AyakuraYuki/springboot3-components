package cc.ayakurayuki.spring.components.stats.model;

import cc.ayakurayuki.spring.components.stats.metrics.AYGauge;

/**
 * @author Ayakura Yuki
 */
public class ThreadPool {

  private static final String NAMESPACE = "thread_pool";

  public static final AYGauge POOL_STATE_ACTIVE = AYGauge.build()
      .namespace(NAMESPACE)
      .subsystem("threads")
      .name("active")
      .help("thread pool current active")
      .labelNames("name")
      .create()
      .register();

  public static final AYGauge POOL_STATE_TASK_WAITING = AYGauge.build()
      .namespace(NAMESPACE)
      .subsystem("threads")
      .name("task_waiting")
      .help("thread pool current task waiting")
      .labelNames("name")
      .create()
      .register();

}
