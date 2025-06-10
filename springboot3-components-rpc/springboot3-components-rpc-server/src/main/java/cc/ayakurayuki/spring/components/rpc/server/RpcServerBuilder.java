package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.rpc.core.NettyUtils;
import io.grpc.BindableService;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.HandlerRegistry;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.io.File;
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

  private NettyServerBuilder delegateBuilder;

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

  // todo

  @Override
  public RpcServerBuilder directExecutor() {
    return null;
  }

  @Override
  public RpcServerBuilder executor(@Nullable Executor executor) {
    return null;
  }

  @Override
  public RpcServerBuilder addService(ServerServiceDefinition service) {
    return null;
  }

  @Override
  public RpcServerBuilder addService(BindableService bindableService) {
    return null;
  }

  @Override
  public RpcServerBuilder fallbackHandlerRegistry(@Nullable HandlerRegistry fallbackRegistry) {
    return null;
  }

  @Override
  public RpcServerBuilder useTransportSecurity(File certChain, File privateKey) {
    return null;
  }

  @Override
  public RpcServerBuilder decompressorRegistry(@Nullable DecompressorRegistry registry) {
    return null;
  }

  @Override
  public RpcServerBuilder compressorRegistry(@Nullable CompressorRegistry registry) {
    return null;
  }

  @Override
  public Server build() {
    return null;
  }

}
