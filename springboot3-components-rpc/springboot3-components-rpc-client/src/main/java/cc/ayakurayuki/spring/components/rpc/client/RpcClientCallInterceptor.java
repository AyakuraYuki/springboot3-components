package cc.ayakurayuki.spring.components.rpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Deadline;
import io.grpc.MethodDescriptor;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RpcClientCallInterceptor implements ClientInterceptor {

  private final String name;

  public RpcClientCallInterceptor(String name) {
    if (name == null || name.isBlank()) {
      name = "unknown";
    }
    this.name = name;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    Duration duration = callOptions.getOption(CallOptionKeys.TIMEOUT_KEY);
    Deadline deadline = callOptions.getDeadline();
    // with timeout and without deadline
    if (duration != null && deadline == null) {
      callOptions = callOptions.withDeadlineAfter(duration.toNanos(), TimeUnit.NANOSECONDS);
    }
    return new RpcClientCall<>(name, next.newCall(method, callOptions), method.getFullMethodName());
  }

}
