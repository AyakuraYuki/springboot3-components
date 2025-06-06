package cc.ayakurayuki.spring.components.env;

/**
 * Environment key for described an environment
 *
 * @param <V>
 */
public record Key<V>(
    String name,
    Marshaler<V> marshaller,
    V defaultValue,
    Key<V> fallback
) {

  public Key(String name, Marshaler<V> marshaller) {
    this(name, marshaller, null, null);
  }

  public Key(String name, Marshaler<V> marshaller, V defaultValue) {
    this(name, marshaller, defaultValue, null);
  }

  public Key(String name, Marshaler<V> marshaller, Key<V> fallback) {
    this(name, marshaller, null, fallback);
  }

  public Key(String name, Marshaler<V> marshaller, V defaultValue, Key<V> fallback) {
    this.name = name;
    this.marshaller = marshaller;
    this.defaultValue = defaultValue;
    this.fallback = fallback;
  }

  V get(String value) {
    if (value == null) {
      return defaultValue;
    }
    V v = marshaller.parse(value);
    return v == null ? defaultValue : v;
  }

}
