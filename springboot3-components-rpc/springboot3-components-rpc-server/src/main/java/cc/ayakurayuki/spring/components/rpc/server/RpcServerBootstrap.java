package cc.ayakurayuki.spring.components.rpc.server;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;

public class RpcServerBootstrap implements BootstrapSource {

  @Override
  public String[] packages() {
    return new String[]{
        "io.grpc",
        "io.netty.bootstrap",
        "io.netty.util",
        "io.netty.channel",
        "io.netty.buffer",
        "io.netty.handler.codec"
    };
  }

}
