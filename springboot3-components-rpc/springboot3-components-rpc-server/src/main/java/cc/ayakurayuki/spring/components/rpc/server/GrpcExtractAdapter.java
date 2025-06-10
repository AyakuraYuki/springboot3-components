package cc.ayakurayuki.spring.components.rpc.server;

import io.grpc.Metadata;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Objects;

final class GrpcExtractAdapter implements TextMapGetter<Metadata> {

  static final GrpcExtractAdapter GETTER = new GrpcExtractAdapter();

  @Override
  public Iterable<String> keys(Metadata carrier) {
    return carrier.keys();
  }

  @Nullable
  @Override
  public String get(@Nullable Metadata carrier, @Nonnull String key) {
    if (Objects.isNull(carrier)) {
      return null;
    }
    return carrier.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
  }

}
