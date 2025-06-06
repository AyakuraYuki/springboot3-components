package cc.ayakurayuki.spring.components.context;

import java.util.Objects;

/**
 * @author Yann
 */
public class Key<T> {

  private final String name;
  private final T      defaultValue;

  public Key(String name) {
    this(name, null);
  }

  public Key(String name, T defaultValue) {
    this.name = name;
    this.defaultValue = defaultValue;
  }

  /**
   * Get the value from the {@link Context#current()} context for this key.
   */
  public T get() {
    return get(Context.current());
  }

  /**
   * Get the value from the specified context for this key.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public T get(Context context) {
    T value = context != null ? (T) context.lookup(this) : null;
    return value == null ? defaultValue : value;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Key<?> key = (Key<?>) o;
    return Objects.equals(name, key.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
