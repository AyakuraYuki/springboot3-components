package cc.ayakurayuki.spring.components.env;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
class EnvironmentValueHolder<T> {

  /**
   * actual name
   */
  private final String name;

  /**
   * actual value
   */
  private final T value;

  public String name() {
    return name;
  }

  public T value() {
    return value;
  }

}
