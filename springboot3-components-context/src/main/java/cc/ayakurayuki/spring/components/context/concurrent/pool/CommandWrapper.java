package cc.ayakurayuki.spring.components.context.concurrent.pool;

import java.util.concurrent.Callable;

/**
 * wrap command with multiple contexts
 *
 * @author Ayakura Yuki
 */
public class CommandWrapper {

  public static Runnable wrap(Runnable command) {
    command = io.opentelemetry.context.Context.current().wrap(command); // opentelemetry enhancement
    return command;
  }

  public static <V> Callable<V> wrap(Callable<V> callable) {
    callable = io.opentelemetry.context.Context.current().wrap(callable); // opentelemetry enhancement
    return callable;
  }

}
