package cc.ayakurayuki.spring.components.context;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Context
 *
 * @param <PK> parameter key type
 * @param <PV> parameter value type
 * @param <MK> metadata key type
 * @param <MV> metadata value type
 */
@Slf4j
@Data
@SuppressWarnings("rawtypes")
public abstract class Context<PK, PV, MK, MV> {

  static ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();

  private          String              debugParameter; // generate a human-readable form parameter
  private          Map<PK, PV>         parameter;      // request parameters
  private          String              debugHeader;    // generate a human-readable form header
  private          Map<MK, MV>         metadata;       // request meta http header or grpc meta
  private          User                user;           // identified access user
  private          IP                  ip;             // request ip detail
  private          String              path;           // requested path
  private          String              realPath;       // the actual requested path contains parameter values
  private          String              caller;         // request caller
  private          Throwable           exception;      // throw exception
  private          boolean             mirror;         // identifier to the mirror resource
  private volatile Map<Key<?>, Object> keyValues;      // key-values
  private volatile Map<String, Object> logMetadata;    // log metadata
  private volatile int                 error;          // error tag

  public static <PK, PV, MK, MV> Context<PK, PV, MK, MV> empty() {
    return EmptyContextFactory.create();
  }

  /**
   * Create a {@link Key} with the given debug name. Multiple different keys
   * may have the same name; the name is intended for debugging purposes and
   * does not impact behavior.
   */
  public static <T> Key<T> key(String name) {
    return new Key<>(name);
  }

  /**
   * Create a {@link Key} with the given debug name and default value. Multiple
   * different keys may have the same name; the name is intended for debugging
   * purposes and does not impact behavior.
   */
  public static <T> Key<T> keyWithDefault(String name, T defaultValue) {
    return new Key<>(name, defaultValue);
  }

  @Nonnull
  public static Context current() {
    Context context = contextThreadLocal.get();
    if (context == null) {
      return empty();
    }
    return context;
  }

  /**
   * add log metadata to current context
   *
   * @param k log key
   * @param v log value
   */
  public Context<PK, PV, MK, MV> withLogMetadata(String k, Object v) {
    if (isEmpty()) {
      return this;
    }
    Map<String, Object> values = new HashMap<>();
    if (this.logMetadata != null) {
      values.putAll(this.logMetadata);
    }
    values.put(k, v);
    this.logMetadata = values;
    return this;
  }

  /**
   * add log metadata to current context
   *
   * @param kv log key value
   */
  public Context<PK, PV, MK, MV> withLogMetadata(Map<String, Object> kv) {
    if (isEmpty()) {
      return this;
    }
    Map<String, Object> values = new HashMap<>();
    if (this.logMetadata != null) {
      values.putAll(this.logMetadata);
    }
    values.putAll(kv);
    this.logMetadata = values;
    return this;
  }

  /**
   * get all log metadata
   */
  public Map<String, Object> getLogMetadata() {
    if (this.logMetadata == null) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(this.logMetadata);
  }

  /**
   * append key and values to context
   *
   * @param key key object
   * @param v   value
   * @param <V> type of value
   */
  public <V> Context<PK, PV, MK, MV> withValue(Key<V> key, V v) {
    if (isEmpty()) {
      return this;
    }
    Map<Key<?>, Object> values = new HashMap<>();
    if (this.keyValues != null) {
      values.putAll(this.keyValues);
    }
    values.put(key, v);
    this.keyValues = values;
    return this;
  }

  /**
   * lookup by given key object
   */
  public Object lookup(Key key) {
    return this.keyValues != null ? this.keyValues.get(key) : null;
  }

  public Throwable getException() {
    return this.exception;
  }

  /**
   * WARNING: Do NOT call this method manual.
   */
  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * Attach this context, thus enter a new scope within which this context is {@link #current}.
   */
  public Context attach() {
    Context previous = current();
    contextThreadLocal.set(this);
    return previous;
  }

  /**
   * Reverse an {@code attach()}, restoring the previous context and exiting the current scope.
   *
   * <p>
   * This context should be the same context that was previously {@link #attach attached}.
   * The provided replacement should be what was returned by the same {@link #attach attach()}
   * call. If an {@code attach()} and a {@code detach()} meet above requirements, they match.
   */
  public void detach(@Nullable Context context) {
    if (context != empty()) {
      contextThreadLocal.set(context);
    } else {
      contextThreadLocal.remove();
    }
  }

  /**
   * mark context is success
   */
  public void markSuccess() {
    error = 0;
  }

  /***
   * mark context is fail
   */
  public void markFail() {
    error = 1;
  }

  private boolean isEmpty() {
    return this == empty();
  }

  // ----------------------------------------------------------------------------------------------------

  /**
   * Execute a {@link Runnable} function immediately with this context as the {@link #current()} context.
   */
  public void run(Runnable r) {
    Preconditions.checkNotNull(r, "runner");
    Context previous = attach();
    try {
      r.run();
    } finally {
      detach(previous);
    }
  }

  /**
   * Execute a {@link Supplier} function immediately with this context as the {@link #current()} context.
   */
  public <V> V supplier(Supplier<V> c) {
    Preconditions.checkNotNull(c, "supplier");
    Context previous = attach();
    try {
      return c.get();
    } finally {
      detach(previous);
    }
  }

  /**
   * Execute a {@link Callable} function immediately with this context as the {@link #current()} context.
   */
  public <V> V callable(Callable<V> c) throws Exception {
    Preconditions.checkNotNull(c, "call");
    Context previous = attach();
    try {
      return c.call();
    } finally {
      detach(previous);
    }
  }

  /**
   * Wrap a {@link Runnable} so that it executes with this context as the {@link #current()} context.
   */
  public Runnable wrap(Runnable r) {
    Preconditions.checkNotNull(r, "runner");
    return () -> {
      Context previous = attach();
      try {
        r.run();
      } finally {
        detach(previous);
      }
    };
  }

  /**
   * Wrap a {@link Supplier} so that it executes with this context as the {@link #current()} context.
   */
  public <V> Supplier<V> wrap(Supplier<V> s) {
    Preconditions.checkNotNull(s, "supplier");
    return () -> {
      Context previous = attach();
      try {
        return s.get();
      } finally {
        detach(previous);
      }
    };
  }

  /**
   * Wrap a {@link Callable} so that it executes with this context as the {@link #current()} context.
   */
  public <V> Callable<V> wrap(Callable<V> c) {
    Preconditions.checkNotNull(c, "call");
    return () -> {
      Context previous = attach();
      try {
        return c.call();
      } finally {
        detach(previous);
      }
    };
  }

}

