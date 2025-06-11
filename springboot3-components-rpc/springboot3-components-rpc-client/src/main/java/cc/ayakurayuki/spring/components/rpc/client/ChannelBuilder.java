package cc.ayakurayuki.spring.components.rpc.client;

import cc.ayakurayuki.spring.components.rpc.core.NettyUtils;
import io.grpc.ClientInterceptor;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.NameResolver;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelBuilder extends io.grpc.ManagedChannelBuilder<ChannelBuilder> {

  private static final EventLoopGroup LOOP_GROUP = NettyUtils.eventLoopGroup("RPC-client", Runtime.getRuntime().availableProcessors() * 2, true);

  static {
    if (NettyUtils.isEpollAvailable()) {
      log.info("rpc client is using netty epoll IO implementation");
    }
  }

  private final NettyChannelBuilder delegateBuilder;

  private ChannelBuilder(String target) {
    this.delegateBuilder = NettyChannelBuilder.forTarget(target);
    this.setupChannel(this.delegateBuilder);
  }

  private ChannelBuilder(String host, int post) {
    this.delegateBuilder = NettyChannelBuilder.forAddress(host, post);
    this.setupChannel(this.delegateBuilder);
  }

  public static ChannelBuilder forTarget(String target) {
    return new ChannelBuilder(target);
  }

  public static ChannelBuilder forAddress(String host, int post) {
    return new ChannelBuilder(host, post);
  }

  private void setupChannel(NettyChannelBuilder channelBuilder) {
    channelBuilder.channelType(NettyUtils.socketChannel())
        .eventLoopGroup(LOOP_GROUP)
        .withOption(ChannelOption.TCP_NODELAY, true);
  }

  public ChannelBuilder connectTimeout(long timeout, @Nonnull TimeUnit timeUnit) {
    this.delegateBuilder.withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeUnit.toMillis(timeout));
    return this;
  }

  /**
   * Sets the flow control window in bytes. Setting flowControlWindow disables auto flow control
   * tuning; use {@link NettyChannelBuilder#initialFlowControlWindow(int)} to enable auto flow control tuning. If not
   * called, the default value is {@link NettyChannelBuilder#DEFAULT_FLOW_CONTROL_WINDOW}) with auto flow control
   * tuning.
   */
  public ChannelBuilder flowControlWindow(int flowControlWindow) {
    this.delegateBuilder.flowControlWindow(flowControlWindow);
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
  @Override
  public ChannelBuilder maxInboundMetadataSize(int bytes) {
    this.delegateBuilder.maxInboundMetadataSize(bytes);
    return this;
  }

  /**
   * Sets the maximum message size allowed for a single gRPC frame. If an inbound messages larger
   * than this limit is received it will not be processed and the RPC will fail with
   * RESOURCE_EXHAUSTED.
   */
  @Override
  public ChannelBuilder maxInboundMessageSize(int bytes) {
    this.delegateBuilder.maxInboundMessageSize(bytes);
    return this;
  }

  @Override
  public ChannelBuilder directExecutor() {
    this.delegateBuilder.directExecutor();
    return this;
  }

  @Override
  public ChannelBuilder executor(Executor executor) {
    this.delegateBuilder.executor(executor);
    return this;
  }

  @Override
  public ChannelBuilder intercept(List<ClientInterceptor> interceptors) {
    this.delegateBuilder.intercept(interceptors);
    return this;
  }

  @Override
  public ChannelBuilder intercept(ClientInterceptor... interceptors) {
    this.delegateBuilder.intercept(interceptors);
    return this;
  }

  @Override
  public ChannelBuilder userAgent(String userAgent) {
    this.delegateBuilder.userAgent(userAgent);
    return this;
  }

  @Override
  public ChannelBuilder overrideAuthority(String authority) {
    this.delegateBuilder.overrideAuthority(authority);
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public ChannelBuilder nameResolverFactory(NameResolver.Factory resolverFactory) {
    this.delegateBuilder.nameResolverFactory(resolverFactory);
    return this;
  }

  @Override
  public ChannelBuilder defaultLoadBalancingPolicy(String policy) {
    this.delegateBuilder.defaultLoadBalancingPolicy(policy);
    return this;
  }

  @Override
  public ChannelBuilder decompressorRegistry(DecompressorRegistry registry) {
    this.delegateBuilder.decompressorRegistry(registry);
    return this;
  }

  @Override
  public ChannelBuilder compressorRegistry(CompressorRegistry registry) {
    this.delegateBuilder.compressorRegistry(registry);
    return this;
  }

  @Override
  public ChannelBuilder idleTimeout(long value, TimeUnit unit) {
    this.delegateBuilder.idleTimeout(value, unit);
    return this;
  }

  @Override
  public ChannelBuilder maxRetryAttempts(int maxRetryAttempts) {
    this.delegateBuilder.maxRetryAttempts(maxRetryAttempts);
    return this;
  }

  @Override
  public ChannelBuilder maxHedgedAttempts(int maxHedgedAttempts) {
    this.delegateBuilder.maxHedgedAttempts(maxHedgedAttempts);
    return this;
  }

  @Override
  public ChannelBuilder retryBufferSize(long bytes) {
    this.delegateBuilder.retryBufferSize(bytes);
    return this;
  }

  @Override
  public ChannelBuilder perRpcBufferLimit(long bytes) {
    this.delegateBuilder.perRpcBufferLimit(bytes);
    return this;
  }

  @Override
  public ChannelBuilder disableRetry() {
    this.delegateBuilder.disableRetry();
    return this;
  }

  @Override
  public ChannelBuilder enableRetry() {
    this.delegateBuilder.enableRetry();
    return this;
  }

  @Override
  public ChannelBuilder usePlaintext() {
    this.delegateBuilder.usePlaintext();
    return this;
  }

  @Override
  public ChannelBuilder useTransportSecurity() {
    this.delegateBuilder.useTransportSecurity();
    return this;
  }

  @Override
  public ChannelBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
    this.delegateBuilder.keepAliveTime(keepAliveTime, timeUnit);
    return this;
  }

  @Override
  public ChannelBuilder keepAliveTimeout(long keepAliveTimeout, TimeUnit timeUnit) {
    this.delegateBuilder.keepAliveTimeout(keepAliveTimeout, timeUnit);
    return this;
  }

  @Override
  public ChannelBuilder keepAliveWithoutCalls(boolean enable) {
    this.delegateBuilder.keepAliveWithoutCalls(enable);
    return this;
  }

  @Override
  public ManagedChannel build() {
    return this.delegateBuilder.build();
  }

}
