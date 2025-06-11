package cc.ayakurayuki.spring.components.rpc.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class StatusProto {

  private static final Metadata.Key<com.google.rpc.Status> STATUS_DETAILS_KEY = Metadata.Key.of(
      "grpc-status-details-bin",
      ProtoUtils.metadataMarshaller(com.google.rpc.Status.getDefaultInstance())
  );

  public static Status toStatus(@Nonnull com.google.rpc.Status statusProto) {
    Status status = Status.fromCodeValue(statusProto.getCode());
    checkArgument(status.getCode().value() == statusProto.getCode(), "invalid status code");
    return status.withDescription(statusProto.getMessage());
  }

  public static Metadata toMetadata(@Nonnull com.google.rpc.Status statusProto, @Nonnull Metadata metadata) {
    checkNotNull(metadata, "metadata must not be null");
    metadata.discardAll(STATUS_DETAILS_KEY);
    metadata.put(STATUS_DETAILS_KEY, statusProto);
    return metadata;
  }

  public static Metadata toMetadata(com.google.rpc.Status statusProto) {
    Metadata metadata = new Metadata();
    metadata.put(STATUS_DETAILS_KEY, statusProto);
    return metadata;
  }

  /**
   * Extracts the {@code google.rpc.Status} from trailers, and makes sure they match the gRPC {@code status}. If the trailers do not contain a {@code google.rpc.Status}, it uses {@code status} param
   * to generate a {@code google.rpc.Status}.
   *
   * @return the embedded google.rpc.Status
   *
   * @since 1.11.0
   */
  public static com.google.rpc.Status fromStatusAndTrailers(@Nonnull Status status, @Nullable Metadata trailers) {
    checkNotNull(status, "status");

    if (trailers != null) {
      com.google.rpc.Status statusProto = trailers.get(STATUS_DETAILS_KEY);
      if (statusProto != null) {
        checkArgument(status.getCode().value() == statusProto.getCode(), "com.google.rpc.Status code must match gRPC status code");
        return statusProto;
      }
    }

    // fall-back to status, this is useful if the error is local. e.g., Server is unavailable.
    com.google.rpc.Status.Builder statusBuilder = com.google.rpc.Status.newBuilder()
        .setCode(status.getCode().value());

    if (status.getDescription() != null) {
      statusBuilder.setMessage(status.getDescription());
    }

    return statusBuilder.build();
  }

}
