package cc.ayakurayuki.spring.components.rpc.core;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Metadata;
import io.grpc.Status;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class StatusCode {

  /**
   * extract code from grpc {@link Status} and {@link Metadata} trailers
   *
   * @param status   grpc status
   * @param trailers grpc trailers
   *
   * @return code from {@link ServerCode}
   */
  public static int toCode(Status status, Metadata trailers) {
    return toServerCode(status, trailers).code();
  }

  /**
   * extract {@link ServerCode} from grpc {@link Status} and {@link Metadata} trailers
   *
   * @param status   grpc status
   * @param trailers grpc trailers
   *
   * @return {@link ServerCode} instance
   */
  @Nonnull
  public static ServerCode toServerCode(@Nonnull Status status, @Nullable Metadata trailers) {
    if (!status.isOk()) {
      ServerCode sc = convertStatusDetails(status, trailers);
      if (sc != null) {
        return sc;
      }
    }
    return convertStatus(status);
  }

  /**
   * extract {@link ServerCode} from {@code grpc-status-details-bin} via grpc {@link Status} and {@link Metadata} trailers
   *
   * @param status   grpc status
   * @param trailers grpc trailers
   *
   * @return {@link ServerCode} instance, could be null
   */
  @Nullable
  private static ServerCode convertStatusDetails(Status status, Metadata trailers) {
    // analyse grpc-status-details-bin
    com.google.rpc.Status statusPB = StatusProto.fromStatusAndTrailers(status, trailers);
    if (statusPB.getDetailsCount() <= 0) {
      return null;
    }
    for (int i = statusPB.getDetailsCount() - 1; i >= 0; i--) {
      Any detail = Iterables.get(statusPB.getDetailsList(), i, null);
      if (detail == null) {
        continue;
      }
      try {
        if (detail.is(cc.ayakurayuki.aymicro.rpc.Status.class)) {
          cc.ayakurayuki.aymicro.rpc.Status st = detail.unpack(cc.ayakurayuki.aymicro.rpc.Status.class);
          return ServerCode.create(st.getCode(), st.getMessage());
        }
      } catch (InvalidProtocolBufferException ignored) {
      }
    }
    return null;
  }

  /**
   * extract {@link ServerCode} from grpc {@link Status}
   *
   * @param status grpc status
   *
   * @return {@link ServerCode} instance
   */
  @VisibleForTesting
  static ServerCode convertStatus(Status status) {
    Status.Code code = status.getCode();
    return switch (code) {
      case OK -> ServerCode.SUCCESS;
      case CANCELLED -> ServerCode.CLIENT_CLOSE_REQUEST;
      case INVALID_ARGUMENT, FAILED_PRECONDITION, OUT_OF_RANGE -> ServerCode.BAD_REQUEST;
      case DEADLINE_EXCEEDED -> ServerCode.DEADLINE_EXCEEDED;
      case NOT_FOUND -> ServerCode.NOT_FOUND;
      case ALREADY_EXISTS, ABORTED -> ServerCode.CONFLICT;
      case PERMISSION_DENIED -> ServerCode.FORBIDDEN;
      case UNAUTHENTICATED -> ServerCode.UNAUTHORIZED;
      case RESOURCE_EXHAUSTED -> ServerCode.TOO_MANY_REQUESTS;
      case UNIMPLEMENTED -> ServerCode.NOT_IMPLEMENTED;
      case UNAVAILABLE -> ServerCode.SERVICE_UNAVAILABLE;
      default -> ServerCode.SERVER_ERROR;
    };
  }

}
