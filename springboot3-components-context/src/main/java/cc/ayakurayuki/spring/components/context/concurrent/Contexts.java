package cc.ayakurayuki.spring.components.context.concurrent;

import cc.ayakurayuki.spring.components.context.Context;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Contexts {

  public static void setErrorY() {
    Context context = Context.current();
    context.setError(1);
  }

  public static boolean isError() {
    Context context = Context.current();
    return context.getError() == 1;
  }

  public static void set5xxNoLog() {
    Context context = Context.current();
    context.setError(-1);
  }

  public static boolean is5xxWithLog() {
    Context context = Context.current();
    return context.getError() != -1;
  }

  public static void setError(int error) {
    Context context = Context.current();
    context.setError(error);
  }

  /**
   * wrap a {@link Runnable} with {@link Context}
   *
   * @param r the given runnable to be wrapped
   *
   * @return wrapped runnable
   */
  public static Runnable runnable(Runnable r) {
    return Context.current().wrap(r);
  }

  /**
   * wrap a {@link Callable} with {@link Context}
   *
   * @param c the given callable to be wrapped
   *
   * @return wrapped callable
   */
  public static <V> Callable<V> callable(Callable<V> c) {
    return Context.current().wrap(c);
  }

  /**
   * wrap a {@link Function} with {@link Context}
   *
   * @param f the given function to be wrapped
   *
   * @return wrapped function
   */
  public static <T, R> Function<T, R> function(Function<T, R> f) {
    Context context = Context.current();
    return t -> {
      Context prev = context.attach();
      try {
        return f.apply(t);
      } finally {
        context.detach(prev);
      }
    };
  }

  /**
   * wrap a {@link BiFunction} with {@link Context}
   *
   * @param f the given biFunction to be wrapped
   *
   * @return wrapped biFunction
   */
  public static <T, U, R> BiFunction<T, U, R> biFunction(BiFunction<T, U, R> f) {
    Context context = Context.current();
    return (v, u) -> {
      Context prev = context.attach();
      try {
        return f.apply(v, u);
      } finally {
        context.detach(prev);
      }
    };
  }

  /**
   * wrap a {@link Supplier} with {@link Context}
   *
   * @param s the given supplier to be wrapped
   *
   * @return wrapped supplier
   */
  public static <T> Supplier<T> supplier(Supplier<T> s) {
    Context context = Context.current();
    return () -> {
      Context prev = context.attach();
      try {
        return s.get();
      } finally {
        context.detach(prev);
      }
    };
  }

  /**
   * wrap a {@link Consumer} with {@link Context}
   *
   * @param c the given consumer to be wrapped
   *
   * @return wrapped consumer
   */
  public static <T> Consumer<T> consumer(Consumer<T> c) {
    Context context = Context.current();
    return v -> {
      Context prev = context.attach();
      try {
        c.accept(v);
      } finally {
        context.detach(prev);
      }
    };
  }

  /**
   * wrap a {@link BiConsumer} with {@link Context}
   *
   * @param c the given biConsumer to be wrapped
   *
   * @return wrapped biConsumer
   */
  public static <T, U> BiConsumer<T, U> biConsumer(BiConsumer<T, U> c) {
    Context context = Context.current();
    return (v, u) -> {
      Context prev = context.attach();
      try {
        c.accept(v, u);
      } finally {
        context.detach(prev);
      }
    };
  }

}
