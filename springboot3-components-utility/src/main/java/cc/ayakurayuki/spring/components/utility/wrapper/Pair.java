package cc.ayakurayuki.spring.components.utility.wrapper;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * @author Ayakura Yuki
 */
public record Pair<A, B>(A a, B b) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(a(), pair.a())
        && Objects.equals(b(), pair.b());
  }

  @Override
  public int hashCode() {
    return Objects.hash(a(), b());
  }

  @Override
  @NonNull
  public String toString() {
    return "(%s, %s)".formatted(a(), b());
  }

}
