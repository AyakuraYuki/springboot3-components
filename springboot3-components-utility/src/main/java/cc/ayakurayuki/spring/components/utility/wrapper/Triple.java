package cc.ayakurayuki.spring.components.utility.wrapper;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * @author Ayakura Yuki
 */
public record Triple<A, B, C>(A a, B b, C c) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
    return Objects.equals(a(), triple.a())
        && Objects.equals(b(), triple.b())
        && Objects.equals(c(), triple.c());
  }

  @Override
  public int hashCode() {
    return Objects.hash(a(), b(), c());
  }

  @Override
  @NonNull
  public String toString() {
    return "(%s, %s, %s)".formatted(a(), b(), c());
  }

}
