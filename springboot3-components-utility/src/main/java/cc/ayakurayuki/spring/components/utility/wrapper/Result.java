package cc.ayakurayuki.spring.components.utility.wrapper;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Result implements a Rust-like pattern, returning Ok or Err.
 *
 * @author Ayakura Yuki
 */
public record Result<Ok, Err extends Throwable>(Ok ok, Err err) implements IsErr {

  public static <Ok, Err extends Throwable> Result<Ok, Err> ok(@Nullable Ok ok) {
    return new Result<>(ok, null);
  }

  public static <Ok, Err extends Throwable> Result<Ok, Err> err(@NonNull Err err) {
    return new Result<>(null, err);
  }

  public static <Ok, Err extends Throwable> Result<Ok, Err> err(@Nullable Ok ok, @NonNull Err err) {
    return new Result<>(ok, err);
  }

  @Override
  public boolean isErr() {
    return this.err != null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (this == o) {
      return true;
    }
    Result<?, ?> result = (Result<?, ?>) o;
    return Objects.equals(ok(), result.ok())
        && Objects.equals(err(), result.err());
  }

  @Override
  public int hashCode() {
    return Objects.hash(ok(), err());
  }

  @Override
  @NonNull
  public String toString() {
    return "(%s) %s error%s".formatted(
        ok(),
        isErr() ? "with" : "without",
        isErr() ? ": %s".formatted(err().getMessage()) : ""
    );
  }

}
