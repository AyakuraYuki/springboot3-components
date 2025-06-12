package cc.ayakurayuki.spring.components.starter.rpc.client;

import cc.ayakurayuki.spring.components.context.concurrent.pool.ThreadPools;
import cc.ayakurayuki.spring.components.rpc.client.ChannelBuilder;
import cc.ayakurayuki.spring.components.rpc.client.RpcClientCallInterceptor;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RPCNamingClientChannelFactory {

  // scalable thread executor
  // private static final Executor CLIENT_WORKER = ThreadPools.newScalableThreadPool("RPC-Client-exec", 1, Runtime.getRuntime().availableProcessors() * 2);

  // virtual thread executor
  private static final Executor CLIENT_WORKER = ThreadPools.newVirtualExecutor("RPC-Client-exec");

  private final RPCChannelsProperties channelsProperties;

  public RPCNamingClientChannelFactory(RPCChannelsProperties channelsProperties) {
    this.channelsProperties = channelsProperties;
  }

  RPCChannelProperties channelProperties(String name) {
    return this.channelsProperties.getChannel(name);
  }

  Channel createChannel(String name) {
    RPCChannelProperties properties = channelProperties(name);

    // direct mode
    String[] hostParams = properties.getDirect().split(":");
    if (hostParams.length != 2 || Strings.isNullOrEmpty(hostParams[0]) || Strings.isNullOrEmpty(hostParams[1])) {
      throw new IllegalStateException("parse direct url of RPC service %s error".formatted(name));
    }
    Integer port = Ints.tryParse(hostParams[1]);
    if (port == null) {
      throw new IllegalStateException("parse port of RPC service %s error".formatted(name));
    }

    ChannelBuilder builder = ChannelBuilder.forAddress(hostParams[0], port)
        .executor(CLIENT_WORKER)
        .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
        .flowControlWindow(properties.getFlowControlWindow())
        .maxInboundMessageSize(properties.getMaxInboundMessageSize())
        .maxInboundMetadataSize(properties.getMaxInboundMetadataSize())
        .disableRetry();

    if (properties.isPlaintext()) {
      builder.usePlaintext();
    }

    if (properties.isEnableKeepAlive()) {
      builder.keepAliveWithoutCalls(properties.isKeepAliveWithoutCalls())
          .keepAliveTime(properties.getKeepAliveTime(), TimeUnit.MILLISECONDS)
          .keepAliveTimeout(properties.getKeepAliveTimeout(), TimeUnit.MILLISECONDS)
          .idleTimeout(properties.getIdleTimeout(), TimeUnit.SECONDS);
    }

    ManagedChannel channel = builder.build();
    requestConnection(name, properties, channel);
    return ClientInterceptors.intercept(channel, Lists.newArrayList(new RpcClientCallInterceptor(name)));
  }

  private void requestConnection(String name, RPCChannelProperties properties, ManagedChannel channel) {
    // wait channel connection ready
    ConnectivityState currentChannelState = channel.getState(true);

    while (currentChannelState != ConnectivityState.READY) {

      CountDownLatch latchUntilChannelReady = new CountDownLatch(1);
      channel.notifyWhenStateChanged(currentChannelState, latchUntilChannelReady::countDown);
      try {

        latchUntilChannelReady.await();
        currentChannelState = channel.getState(false);

        if (currentChannelState == ConnectivityState.TRANSIENT_FAILURE) {
          String message;

          if (Strings.isNullOrEmpty(properties.getDirect())) {
            message = "RPC service %s requests channel connection failed".formatted(name);
          } else {
            message = "RPC service %s requests channel connection directly to address %s failed".formatted(name, properties.getDirect());
          }

          if (properties.isOptional()) {
            log.warn(message);
            break;
          } else {
            throw new IllegalStateException(message);
          }
        }

      } catch (InterruptedException ignored) {
        throw new IllegalStateException("RPC channel creation interrupted");
      }

    }

    log.info("RPC service %s requests channel connection, state: %s".formatted(name, currentChannelState));
  }

}
