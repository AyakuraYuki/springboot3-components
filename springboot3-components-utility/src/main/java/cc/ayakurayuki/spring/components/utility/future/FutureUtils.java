package cc.ayakurayuki.spring.components.utility.future;

import cc.ayakurayuki.spring.components.utility.collection.CollectionUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ayakura Yuki
 */
@Slf4j
public abstract class FutureUtils {

  public static <T> T join(CompletableFuture<T> future, T demotionResult, String invoker, Object... logArgs) {
    try {
      return future.join();
    } catch (Exception e) {
      log.warn("future.join raised an exception, invoker: %s, params: %s".formatted(invoker, Arrays.toString(logArgs)), e);
      return demotionResult;
    }
  }

  public static <T> T joinThrow(CompletableFuture<T> future) {
    try {
      return future.join();
    } catch (Exception e) {
      log.warn("future.join raised an exception", e);
      throw e;
    }
  }

  public static <K, V> CompletableFuture<Map<K, V>> mergeFutures(List<CompletableFuture<Map<K, V>>> futures) {
    if (CollectionUtils.isEmpty(futures)) {
      return CompletableFuture.completedFuture(Collections.emptyMap());
    }
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
        .thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b)));
  }

  public static <T> CompletableFuture<List<T>> mergeListFutures(List<CompletableFuture<T>> futures) {
    if (CollectionUtils.isEmpty(futures)) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
        .thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
  }

}
