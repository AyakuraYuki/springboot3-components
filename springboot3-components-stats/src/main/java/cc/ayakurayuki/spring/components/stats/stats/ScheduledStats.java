package cc.ayakurayuki.spring.components.stats.stats;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Ayakura Yuki
 */
public class ScheduledStats {

  private static final ScheduledExecutorService scheduledStatsExecutorService;

  static {
    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
        1,
        Thread.ofVirtual().name("ScheduledStats-").factory(), // use virtual instead of this: new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ScheduledStats-%d").build()
        new ThreadPoolExecutor.DiscardPolicy()
    );
    executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    executor.setRemoveOnCancelPolicy(true);
    scheduledStatsExecutorService = executor;
  }

  public static void scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
    scheduledStatsExecutorService.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
  }

}
