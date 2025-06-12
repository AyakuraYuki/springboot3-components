package cc.ayakurayuki.spring.components.context.concurrent;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.concurrent.pool.ThreadPools;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.opentelemetry.context.Scope;
import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ContextFutures {

  private static final Executor FUTURE_CALLBACK;

  static {
    FUTURE_CALLBACK = ThreadPools.newVirtualExecutor("Future-Callback-");
  }

  /**
   * Convert a {@link ListenableFuture} to {@link CompletableFuture}.
   * <p>
   * Be warned! Do NOT do anything blocking in {@link CompletableFuture}, otherwise use async api.
   */
  @SuppressWarnings("rawtypes")
  public static <T> CompletableFuture<T> asCompletableFuture(@Nonnull ListenableFuture<T> listenableFuture) {
    Context context = Context.current();
    io.opentelemetry.context.Context otelContext = io.opentelemetry.context.Context.current();
    CompletableFuture<T> completableFuture = new CompletableFuture<>();
    Futures.addCallback(
        listenableFuture,
        new FutureCallback<>() {
          @Override
          public void onSuccess(T result) {
            context.run(() -> {
              try (Scope ignored = otelContext.makeCurrent()) {
                completableFuture.complete(result);
              }
            });
          }

          @Override
          public void onFailure(Throwable t) {
            context.run(() -> {
              try (Scope ignored = otelContext.makeCurrent()) {
                completableFuture.completeExceptionally(t);
              }
            });
          }
        },
        FUTURE_CALLBACK
    );
    return completableFuture;
  }

}
