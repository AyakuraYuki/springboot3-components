package cc.ayakurayuki.spring.components.context.rpc;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.IP;
import com.google.common.base.Strings;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class RpcContext extends Context<FieldDescriptor, Object, String, String> {

  @SuppressWarnings("rawtypes")
  public static RpcContext create(Metadata metadata, ServerCall serverCall) {
    return new RpcContext(metadata, serverCall);
  }

  /**
   * Reference: <a href="https://github.com/grpc/grpc-java/blob/81da3eb95be37fa0647ce8da2e19de96ab84c600/context/src/test/java/io/grpc/ContextTest.java">ContextTest.java - grpc/grpc-java | Github.com</a>
   */
  @SuppressWarnings("rawtypes")
  private RpcContext(Metadata headers, ServerCall serverCall) {
    final Map<String, String> metadata = new HashMap<>();
    for (String key : headers.keys()) {
      if (!key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
        String value = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
        metadata.put(key, value);
      }
    }
    this.setMetadata(metadata);

    SocketAddress addr = serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
    String clientIP = null;
    if (addr instanceof InetSocketAddress isa) {
      clientIP = isa.getAddress().getHostAddress();
    }
    String remoteIP = metadata.get(RpcHeaders.REMOTE_IP);
    if (Strings.isNullOrEmpty(remoteIP)) {
      remoteIP = metadata.getOrDefault(RpcHeaders.X_REAL_IP, clientIP);
    }
    final String title = serverCall.getMethodDescriptor().getFullMethodName();

    Optional.ofNullable(metadata.get(RpcHeaders.MIRROR))
        .map(StringUtils::isNotEmpty)
        .ifPresent(this::setMirror);
    this.setCaller(metadata.getOrDefault(RpcHeaders.CALLER, "unknown"));
    this.setPath(title);
    this.setRealPath(title);
    this.setIp(new IP(clientIP, remoteIP, 0));
  }

  @Override
  public void setDebugParameter(String debugParameter) {
    super.setDebugParameter(debugParameter);
  }

  @Override
  public void setParameter(Map<Descriptors.FieldDescriptor, Object> parameter) {
    super.setParameter(parameter);
  }

}
