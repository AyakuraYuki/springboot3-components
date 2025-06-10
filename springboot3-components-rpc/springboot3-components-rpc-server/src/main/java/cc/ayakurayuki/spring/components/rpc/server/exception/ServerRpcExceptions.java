package cc.ayakurayuki.spring.components.rpc.server.exception;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import cc.ayakurayuki.spring.components.errors.exception.ServerException;
import cc.ayakurayuki.spring.components.rpc.core.StatusCode;
import cc.ayakurayuki.spring.components.rpc.core.StatusProto;
import com.google.protobuf.Any;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerRpcExceptions {

  public static WrappedStatus convertStatus(Throwable e) {
    return switch (e) {
      case ServerException se -> convertStatus(se.getCode(), se.getMessage(), null, null, e);
      case StatusRuntimeException sre -> {
        Status status = sre.getStatus();
        Metadata trailers = sre.getTrailers();
        yield convertStatus(StatusCode.toServerCode(status, trailers), status, trailers, e);
      }
      case StatusException se -> {
        Status status = se.getStatus();
        Metadata trailers = se.getTrailers();
        yield convertStatus(StatusCode.toServerCode(status, trailers), status, trailers, e);
      }
      default -> convertStatus(ServerCode.SERVER_ERROR, null, null, e);
    };
  }

  static WrappedStatus convertStatus(@Nonnull ServerCode code, @Nullable Status status, @Nullable Metadata metadata, @Nullable Throwable cause) {
    return convertStatus(code.code(), code.message(), status, metadata, cause);
  }

  static WrappedStatus convertStatus(int code, String message, @Nullable Status status, @Nullable Metadata trailers, @Nullable Throwable cause) {
    if (status != null && status.getCause() != null) {
      cause = status.getCause();
    }
    if (trailers == null) {
      trailers = new Metadata();
    }
    com.google.rpc.Status ts = transmitStatus(code, message);
    return new WrappedStatus(StatusProto.toStatus(ts).withCause(cause), StatusProto.toMetadata(ts, trailers));
  }

  static com.google.rpc.Status transmitStatus(int code, String message) {
    cc.ayakurayuki.aymicro.rpc.Status status = cc.ayakurayuki.aymicro.rpc.Status.newBuilder()
        .setCode(code)
        .setMessage(message)
        .build();
    ArrayList<Any> details = new ArrayList<>();
    details.add(Any.pack(status));
    return com.google.rpc.Status.newBuilder()
        .setCode(Code.UNKNOWN.value())
        .setMessage(String.valueOf(code))
        .addAllDetails(details)
        .build();
  }

}
