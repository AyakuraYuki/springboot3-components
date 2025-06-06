package cc.ayakurayuki.spring.components.env;

import com.google.common.annotations.VisibleForTesting;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Environment<T> {

  private final String name;
  private final T      value;

  public Environment(String name) {
    this.name = name;
    this.value = null;
  }

  public Environment(String name, T value) {
    this.name = name;
    this.value = value;
  }

  @VisibleForTesting
  String getName() {
    return name;
  }

  private static <T> Environment<T> empty(String name) {
    return new Environment<>(name);
  }


  /**
   * Create a {@link Key} with the given  name. Multiple different keys may have the same name.
   */
  public static <V> Key<V> key(String name, Marshaler<V> marshaller) {
    return new Key<>(name, marshaller);
  }

  /**
   * Create a {@link Key} with the given name and default value. Multiple different keys may have the same name.
   */
  public static <V> Key<V> keyWithDefault(String name, Marshaler<V> marshaller, V defaultValue) {
    return new Key<>(name, marshaller, defaultValue);
  }

  /**
   * Create a {@link Key} with the given name and fallback key. Multiple different keys may have the same name.
   */
  public static <V> Key<V> keyWithFallback(String name, Marshaler<V> marshaller, V defaultValue, Key<V> fallback) {
    return new Key<>(name, marshaller, defaultValue, fallback);
  }

  /**
   * Create a {@link Key} with the given name and fallback key. Multiple different keys may have the same name.
   */
  public static <V> Key<V> keyWithFallback(String name, Marshaler<V> marshaller, Key<V> fallback) {
    return new Key<>(name, marshaller, fallback);
  }

  /**
   * Returns an {@code Environment} describing the given non-{@code null} value.
   *
   * @param key the key to describe, which must be non-{@code null}
   * @param <T> the type of the value
   *
   * @return an {@code Environment} with the value present
   *
   * @throws NullPointerException if value is {@code null}
   */
  public static <T> Environment<T> of(Key<T> key) {
    EnvironmentValueHolder<T> holder = EnvironmentLoader.resolveEnvironment(key);
    return new Environment<>(holder.name(), holder.value());
  }


  /**
   * Returns an {@code Environment} describing the given value, if non-{@code null}, otherwise returns an empty {@code Environment}.
   *
   * @param key the key to describe, which must be non-{@code null}
   * @param <T> the type of the value
   *
   * @return an {@code Environment} with a present value if the specified value is non-{@code null}, otherwise an empty {@code Environment}
   */
  public static <T> Environment<T> ofNullable(Key<T> key) {
    EnvironmentValueHolder<T> holder = EnvironmentLoader.resolveEnvironment(key);
    return holder.value() == null ? empty(holder.name()) : new Environment<>(holder.name(), holder.value());
  }


  /**
   * formatted exception message
   *
   * @return message
   */
  private String message() {
    return String.format("Environment value '%s' is not present, using system environment variable '%s', "
                             + "JVM options '-D%s' or local 'env.properties' file properties '%s' to fix this issue.",
                         name, name.toUpperCase(), name, name);
  }

  /**
   * If a value is present, returns the value, otherwise throws {@code NoSuchElementException}.
   *
   * @return the non-{@code null} value described by this {@code Environment}
   *
   * @throws NoSuchElementException if no value is present
   * @apiNote The preferred alternative to this method is {@link #orElseThrow()}.
   */
  public T get() {
    if (value == null) {
      throw new NoSuchElementException(message());
    }
    return value;
  }


  /**
   * If a value is present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if a value is present, otherwise {@code false}
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * If a value is  not present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if a value is not present, otherwise {@code false}
   */
  public boolean isEmpty() {
    return value == null;
  }

  /**
   * If a value is present, performs the given action with the value, otherwise does nothing.
   *
   * @param action the action to be performed, if a value is present
   *
   * @throws NullPointerException if value is present and the given action is {@code null}
   */
  public void ifPresent(Consumer<? super T> action) {
    if (value != null) {
      action.accept(value);
    }
  }

  /**
   * If a value is present, performs the given action with the value, otherwise performs the given empty-based action.
   *
   * @param action      the action to be performed, if a value is present
   * @param emptyAction the empty-based action to be performed, if no value is present
   *
   * @throws NullPointerException if a value is present and the given action is {@code null}, or no value is present and the given empty-based action is {@code null}.
   */
  public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
    if (value != null) {
      action.accept(value);
    } else {
      emptyAction.run();
    }
  }


  /**
   * If a value is present, returns the value, otherwise returns {@code other}.
   *
   * @param other the value to be returned, if no value is present. May be {@code null}.
   *
   * @return the value, if present, otherwise {@code other}
   */
  public T orElse(T other) {
    return value != null ? value : other;
  }


  /**
   * If a value is present, returns the value, otherwise returns the result produced by the supplying function.
   *
   * @param supplier the supplying function that produces a value to be returned
   *
   * @return the value, if present, otherwise the result produced by the supplying function
   *
   * @throws NullPointerException if no value is present and the supplying function is {@code null}
   */
  public T orElseGet(Supplier<? extends T> supplier) {
    return value != null ? value : supplier.get();
  }


  /**
   * If a value is present, returns the value, otherwise throws {@code NoSuchElementException}.
   *
   * @return the non-{@code null} value described by this {@code Environment}
   *
   * @throws NoSuchElementException if no value is present
   */
  public T orElseThrow() {
    if (value == null) {
      throw new NoSuchElementException(message());
    }
    return value;
  }

  /**
   * If a value is present, returns the value, otherwise throws an exception produced by the exception supplying function.
   *
   * @param <X>               Type of the exception to be thrown
   * @param exceptionSupplier the supplying function that produces an exception to be thrown
   *
   * @return the value, if present
   *
   * @throws X                    if no value is present
   * @throws NullPointerException if no value is present and the exception supplying function is {@code null}
   * @apiNote A method reference to the exception constructor with an empty argument list can be used as the supplier. For example, {@code IllegalStateException::new}
   */
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
      return value;
    } else {
      throw exceptionSupplier.get();
    }
  }

}
