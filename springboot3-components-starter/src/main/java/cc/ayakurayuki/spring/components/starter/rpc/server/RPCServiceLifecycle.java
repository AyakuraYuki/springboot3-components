package cc.ayakurayuki.spring.components.starter.rpc.server;

import io.grpc.Server;
import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

@Slf4j
public class RPCServiceLifecycle implements SmartLifecycle {

  private static final AtomicInteger serverCounter = new AtomicInteger(-1);

  private final    RPCServiceServer serviceServer;
  private volatile Server           server;

  RPCServiceLifecycle(RPCServiceServer serviceServer) {
    this.serviceServer = serviceServer;
  }

  @Override
  public void start() {
    Server localServer = this.server;
    if (localServer == null) {
      this.server = this.serviceServer.createServer();
      try {
        this.server.start();
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
      log.info("RPC server started on port %d".formatted(this.server.getPort()));

      Thread awaitThread = new Thread("RPC-container-%d".formatted(serverCounter.incrementAndGet())) {
        @Override
        public void run() {
          try {
            RPCServiceLifecycle.this.server.awaitTermination();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      };
      awaitThread.setDaemon(false);
      awaitThread.start();
    }
  }

  @Override
  public void stop(@Nonnull Runnable callback) {
    this.stop();
    callback.run();
  }

  @Override
  public void stop() {
    Server localServer = this.server;
    if (localServer != null) {
      localServer.shutdown();
      this.server = null;
      log.info("RPC server is shutdown.");
    }
  }

  @Override
  public boolean isRunning() {
    return this.server != null && !this.server.isShutdown();
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }

}
