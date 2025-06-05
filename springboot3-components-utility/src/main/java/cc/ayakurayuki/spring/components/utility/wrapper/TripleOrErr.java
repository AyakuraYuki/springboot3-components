package cc.ayakurayuki.spring.components.utility.wrapper;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * @author Ayakura Yuki
 */
public record TripleOrErr<A, B, C, E extends Throwable>(A a, B b, C c, E exception) implements IsErr {

  @Override
  public boolean isErr() {
    return exception() != null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TripleOrErr<?, ?, ?, ?> that = (TripleOrErr<?, ?, ?, ?>) o;
    return Objects.equals(a(), that.a())
        && Objects.equals(b(), that.b())
        && Objects.equals(c(), that.c())
        && Objects.equals(exception(), that.exception());
  }

  @Override
  public int hashCode() {
    return Objects.hash(a(), b(), c(), exception());
  }

  @Override
  @NonNull
  public String toString() {
    return "(%s, %s, %s) %s exception%s".formatted(
        a(), b(), c(),
        isErr() ? "with" : "without",
        isErr() ? ": %s".formatted(exception().getMessage()) : ""
    );
  }

}
