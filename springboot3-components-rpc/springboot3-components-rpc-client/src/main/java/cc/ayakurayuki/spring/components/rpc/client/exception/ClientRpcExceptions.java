package cc.ayakurayuki.spring.components.rpc.client.exception;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import cc.ayakurayuki.spring.components.errors.exception.ServerException;
import cc.ayakurayuki.spring.components.rpc.core.StatusCode;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;

public class ClientRpcExceptions {

  public static Throwable fromThrowable(Throwable e) {
    Metadata trailers = null;

    Status status = switch (e) {
      case StatusException se -> {
        trailers = se.getTrailers();
        yield se.getStatus();
      }
      case StatusRuntimeException sre -> {
        trailers = sre.getTrailers();
        yield sre.getStatus();
      }
      default -> null;
    };

    if (status != null) {
      ServerCode serverCode = StatusCode.toServerCode(status, trailers);
      return new ServerException(serverCode);
    }

    return e;
  }

  /**
   * throws specified exception {@link ServerException} from a {@link Throwable}
   */
  public static void throwsBusinessServerException(Throwable e) {
    Throwable error = fromThrowable(e);
    if (error instanceof ServerException se) {
      throw se;
    }
  }

}
