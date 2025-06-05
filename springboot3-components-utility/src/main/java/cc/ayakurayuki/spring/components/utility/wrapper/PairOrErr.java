package cc.ayakurayuki.spring.components.utility.wrapper;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * @author Ayakura Yuki
 */
public record PairOrErr<A, B, E extends Throwable>(A a, B b, E exception) implements IsErr {

  @Override
  public boolean isErr() {
    return exception() != null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PairOrErr<?, ?, ?> pairOrErr = (PairOrErr<?, ?, ?>) o;
    return Objects.equals(a(), pairOrErr.a())
        && Objects.equals(b(), pairOrErr.b())
        && Objects.equals(exception(), pairOrErr.exception());
  }

  @Override
  public int hashCode() {
    return Objects.hash(a(), b(), exception());
  }

  @Override
  @NonNull
  public String toString() {
    return "(%s, %s) %s exception%s".formatted(
        a(), b(),
        isErr() ? "with" : "without",
        isErr() ? ": %s".formatted(exception().getMessage()) : ""
    );
  }

}
