package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.rpc.core.NettyUtils;
import io.grpc.BindableService;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.HandlerRegistry;
import io.grpc.Server;
import io.grpc.ServerInterceptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServerStreamTracer;
import io.grpc.ServerTransportFilter;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServerBuilder extends io.grpc.ServerBuilder<RpcServerBuilder> {

  static {
    if (NettyUtils.isEpollAvailable()) {
      log.info("rpc server is using netty epoll IO implementation");
    }
  }

  private final NettyServerBuilder delegateBuilder;

  private RpcServerBuilder(int port) {
    // eventloop groups
    EventLoopGroup boosGroup = NettyUtils.eventLoopGroup("RPC-server-boss", 1, true);
    EventLoopGroup workerGroup = NettyUtils.eventLoopGroup("RPC-server-worker", Runtime.getRuntime().availableProcessors() * 2, true);

    this.delegateBuilder = NettyServerBuilder.forPort(port);
    this.delegateBuilder.channelType(NettyUtils.serverSocketChannel())
        .bossEventLoopGroup(boosGroup)
        .workerEventLoopGroup(workerGroup)
        .keepAliveTime(10, TimeUnit.SECONDS) // interval for sending ping package
        .keepAliveTimeout(1, TimeUnit.SECONDS) // timeout of ping package
        .permitKeepAliveTime(1, TimeUnit.SECONDS) // minimum interval to keep alive
        .permitKeepAliveWithoutCalls(true) // allow meaningless ping
        .maxConnectionIdle(5, TimeUnit.MINUTES) // maximum idle time
        .maxConnectionAge(2, TimeUnit.HOURS) // maximum connection live time
        .maxConnectionAgeGrace(20, TimeUnit.SECONDS) // interval for graceful close connection
        .withChildOption(ChannelOption.TCP_NODELAY, true) // enable tcp nodelay
        .withChildOption(ChannelOption.SO_REUSEADDR, true); // enable reuse socket address (port)
  }

  public static RpcServerBuilder forPort(int port) {
    return new RpcServerBuilder(port);
  }

  /**
   * Sets a custom keepalive time, the delay time for sending next keepalive ping. An unreasonably
   * small value might be increased, and {@code Long.MAX_VALUE} nano seconds or an unreasonably
   * large value will disable keepalive.
   */
  public RpcServerBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
    this.delegateBuilder.keepAliveTime(keepAliveTime, timeUnit);
    return this;
  }

  /**
   * Sets a custom keepalive timeout, the timeout for keepalive ping requests. An unreasonably small
   * value might be increased.
   */
  public RpcServerBuilder keepAliveTimeout(long keepAliveTimeout, TimeUnit timeUnit) {
    this.delegateBuilder.keepAliveTimeout(keepAliveTimeout, timeUnit);
    return this;
  }

  /**
   * Sets a custom max connection idle time, connection being idle for longer than which will be
   * gracefully terminated. Idleness duration is defined since the most recent time the number of
   * outstanding RPCs became zero or the connection establishment. An unreasonably small value might
   * be increased. {@code Long.MAX_VALUE} nano seconds or an unreasonably large value will disable
   * max connection idle.
   */
  public RpcServerBuilder maxConnectionIdle(long maxConnectionIdle, TimeUnit timeUnit) {
    this.delegateBuilder.maxConnectionIdle(maxConnectionIdle, timeUnit);
    return this;
  }

  /**
   * Sets a custom max connection age, connection lasting longer than which will be gracefully
   * terminated. An unreasonably small value might be increased.  A random jitter of +/-10% will be
   * added to it. {@code Long.MAX_VALUE} nano seconds or an unreasonably large value will disable
   * max connection age.
   */
  public RpcServerBuilder maxConnectionAge(long maxConnectionAge, TimeUnit timeUnit) {
    this.delegateBuilder.maxConnectionAge(maxConnectionAge, timeUnit);
    return this;
  }

  /**
   * Sets a custom grace time for the graceful connection termination. Once the max connection age
   * is reached, RPCs have the grace time to complete. RPCs that do not complete in time will be
   * cancelled, allowing the connection to terminate. {@code Long.MAX_VALUE} nano seconds or an
   * unreasonably large value are considered infinite.
   *
   * @see #maxConnectionAge(long, TimeUnit)
   */
  public RpcServerBuilder maxConnectionAgeGrace(long maxConnectionAgeGrace, TimeUnit timeUnit) {
    this.delegateBuilder.maxConnectionAgeGrace(maxConnectionAgeGrace, timeUnit);
    return this;
  }

  /**
   * Specify the most aggressive keep-alive time clients are permitted to configure. The server will
   * try to detect clients exceeding this rate and when detected will forcefully close the
   * connection. The default is 5 minutes.
   *
   * <p>Even though a default is defined that allows some keep-alives, clients must not use
   * keep-alive without approval from the service owner. Otherwise, they may experience failures in
   * the future if the service becomes more restrictive. When unthrottled, keep-alives can cause a
   * significant amount of traffic and CPU usage, so clients and servers should be conservative in
   * what they use and accept.
   *
   * @see #permitKeepAliveWithoutCalls(boolean)
   */
  public RpcServerBuilder permitKeepAliveTime(long permitKeepAliveTime, TimeUnit timeUnit) {
    this.delegateBuilder.permitKeepAliveTime(permitKeepAliveTime, timeUnit);
    return this;
  }

  /**
   * Sets whether to allow clients to send keep-alive HTTP/2 PINGs even if there are no outstanding
   * RPCs on the connection. Defaults to {@code false}.
   *
   * @see #permitKeepAliveTime(long, TimeUnit)
   */
  public RpcServerBuilder permitKeepAliveWithoutCalls(boolean permit) {
    this.delegateBuilder.permitKeepAliveWithoutCalls(permit);
    return this;
  }

  /**
   * Sets the HTTP/2 flow control window. If not called, the default value is 1m
   */
  public RpcServerBuilder flowControlWindow(int flowControlWindow) {
    this.delegateBuilder.flowControlWindow(flowControlWindow);
    return this;
  }

  /**
   * Sets the maximum message size allowed to be received on the server. If not called, defaults to 4 MiB.
   * The default provides protection to servers who haven't considered the possibility of receiving large
   * messages while trying to be large enough to not be hit in normal usage.
   * <p>
   * This method is advisory, and implementations may decide to not enforce this. Currently, the only
   * known transport to not enforce this is InProcessServer.
   */
  public RpcServerBuilder maxInboundMessageSize(int bytes) {
    this.delegateBuilder.maxInboundMessageSize(bytes);
    return this;
  }

  /**
   * Sets the maximum size of metadata allowed to be received. This is cumulative size of the
   * entries with some overhead, as defined for
   * <a href="http://httpwg.org/specs/rfc7540.html#rfc.section.6.5.2">
   * HTTP/2's SETTINGS_MAX_HEADER_LIST_SIZE</a>. The default is 8 KiB.
   *
   * @param bytes the maximum size of received metadata
   *
   * @return this
   *
   * @throws IllegalArgumentException if bytes is non-positive
   */
  public RpcServerBuilder maxInboundMetadataSize(int bytes) {
    this.delegateBuilder.maxInboundMetadataSize(bytes);
    return this;
  }

  @Override
  public RpcServerBuilder directExecutor() {
    this.delegateBuilder.directExecutor();
    return this;
  }

  @Override
  public RpcServerBuilder executor(@Nullable Executor executor) {
    this.delegateBuilder.executor(executor);
    return this;
  }

  @Override
  public RpcServerBuilder addService(ServerServiceDefinition service) {
    this.delegateBuilder.addService(service);
    return this;
  }

  @Override
  public RpcServerBuilder addService(BindableService bindableService) {
    this.delegateBuilder.addService(bindableService);
    return this;
  }

  @Override
  public RpcServerBuilder fallbackHandlerRegistry(@Nullable HandlerRegistry fallbackRegistry) {
    this.delegateBuilder.fallbackHandlerRegistry(fallbackRegistry);
    return this;
  }

  @Override
  public RpcServerBuilder decompressorRegistry(@Nullable DecompressorRegistry registry) {
    this.delegateBuilder.decompressorRegistry(registry);
    return this;
  }

  @Override
  public RpcServerBuilder compressorRegistry(@Nullable CompressorRegistry registry) {
    this.delegateBuilder.compressorRegistry(registry);
    return this;
  }

  @Override
  public RpcServerBuilder intercept(ServerInterceptor interceptor) {
    this.delegateBuilder.intercept(interceptor);
    return this;
  }

  @Override
  public RpcServerBuilder addTransportFilter(ServerTransportFilter filter) {
    this.delegateBuilder.addTransportFilter(filter);
    return this;
  }

  @Override
  public RpcServerBuilder addStreamTracerFactory(ServerStreamTracer.Factory factory) {
    this.delegateBuilder.addStreamTracerFactory(factory);
    return this;
  }

  @Override
  public RpcServerBuilder useTransportSecurity(File certChain, File privateKey) {
    this.delegateBuilder.useTransportSecurity(certChain, privateKey);
    return this;
  }

  @Override
  public RpcServerBuilder useTransportSecurity(InputStream certChain, InputStream privateKey) {
    this.delegateBuilder.useTransportSecurity(certChain, privateKey);
    return this;
  }

  @Override
  public RpcServerBuilder handshakeTimeout(long timeout, TimeUnit unit) {
    this.delegateBuilder.handshakeTimeout(timeout, unit);
    return this;
  }

  @Override
  public Server build() {
    return this.delegateBuilder.build();
  }

}
