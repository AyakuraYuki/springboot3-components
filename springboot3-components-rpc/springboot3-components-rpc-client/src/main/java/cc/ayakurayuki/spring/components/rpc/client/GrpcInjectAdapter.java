package cc.ayakurayuki.spring.components.rpc.client;

import io.grpc.Metadata;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

final class GrpcInjectAdapter implements TextMapSetter<Metadata> {

  static final GrpcInjectAdapter SETTER = new GrpcInjectAdapter();

  @Override
  public void set(@Nullable Metadata carrier, @Nonnull String key, @Nonnull String value) {
    if (carrier == null) {
      return;
    }
    carrier.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);
  }

}
