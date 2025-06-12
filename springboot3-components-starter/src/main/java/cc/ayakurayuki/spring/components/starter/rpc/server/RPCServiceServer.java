package cc.ayakurayuki.spring.components.starter.rpc.server;

import cc.ayakurayuki.spring.components.context.concurrent.pool.ThreadPools;
import cc.ayakurayuki.spring.components.env.Environment;
import cc.ayakurayuki.spring.components.env.Environments;
import cc.ayakurayuki.spring.components.env.Key;
import cc.ayakurayuki.spring.components.rpc.server.DefaultRpcServerExceptionResolver;
import cc.ayakurayuki.spring.components.rpc.server.RpcServerBuilder;
import cc.ayakurayuki.spring.components.rpc.server.RpcServerInterceptor;
import cc.ayakurayuki.spring.components.rpc.server.additional.MethodAnnotations;
import com.google.common.base.Strings;
import io.grpc.Compressor;
import io.grpc.CompressorRegistry;
import io.grpc.Server;
import io.grpc.ServerMethodDefinition;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RPCServiceServer {

  @SuppressWarnings("unused")
  private static final int CORE_POOL_SIZE    = Runtime.getRuntime().availableProcessors() * 2;
  @SuppressWarnings("unused")
  private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 50;

  private static final Executor WORKER_EXECUTOR = ThreadPools.newVirtualExecutor("RPC-server-exec");

  /**
   * gRPC service port from discovery
   */
  private static final Key<Integer> DISCOVERY_GRPC_PORT = Environment.key("discovery_grpc_port", Environments.INT_MARSHALLER);

  private final List<RPCServiceDefinition> services          = new LinkedList<>();
  private final MethodAnnotations          methodAnnotations = new MethodAnnotations();
  private final RPCServiceProperties       properties;

  public RPCServiceServer(RPCServiceProperties properties) {
    this.properties = properties;
  }

  void addService(RPCServiceDefinition service) {
    this.services.add(service);
  }

  void addMethodAnnotations(RPCServiceDefinition service) {
    if (service == null) {
      return;
    }

    Map<String, String> fullMethodNameByMethod = new HashMap<>();
    for (ServerMethodDefinition<?, ?> m : service.definition().getMethods()) {
      String fullMethodName = m.getMethodDescriptor().getFullMethodName();
      String rpcMethodName = fullMethodName.split("/")[1];
      String methodName = Character.toLowerCase(rpcMethodName.charAt(0)) + rpcMethodName.substring(1);
      fullMethodNameByMethod.put(methodName, fullMethodName);
    }

    Method[] methods = service.beanClass().getMethods();
    for (Method method : methods) {
      Annotation[] annotations = method.getAnnotations();
      if (annotations.length > 0) {
        String fullMethodName = fullMethodNameByMethod.get(method.getName());
        if (!Strings.isNullOrEmpty(fullMethodName)) {
          this.methodAnnotations.attachAttributes(fullMethodName, Arrays.asList(annotations));
        }
      }
    }
  }

  MethodAnnotations getMethodAnnotations() {
    return this.methodAnnotations;
  }

  Server createServer() {
    int port = Environment.ofNullable(DISCOVERY_GRPC_PORT).orElse(this.properties.getPort());
    RpcServerBuilder builder = RpcServerBuilder.forPort(port).executor(WORKER_EXECUTOR);
    for (RPCServiceDefinition service : this.services) {
      String serviceName = service.definition().getServiceDescriptor().getName();
      builder.addService(service.definition());
      log.info("RPC service registered: %s (%s)".formatted(serviceName, service.beanClass().getName()));
    }
    final String compressorName = this.properties.getCompressor();
    Compressor compressor = CompressorRegistry.getDefaultInstance().lookupCompressor(compressorName);
    if (compressor == null) {
      throw new IllegalArgumentException("unable to find compressor named %s".formatted(compressorName));
    }
    return builder
        .flowControlWindow(this.properties.getFlowControlWindow())
        .maxInboundMessageSize(this.properties.getMaxInboundMessageSize())
        .maxInboundMetadataSize(this.properties.getMaxInboundMetadataSize())
        .intercept(new RpcServerInterceptor(compressorName, new DefaultRpcServerExceptionResolver()))
        .build();
  }

}
