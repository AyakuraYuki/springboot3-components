package cc.ayakurayuki.spring.components.rpc.core;

import cc.ayakurayuki.spring.components.stats.model.ThreadPool;
import cc.ayakurayuki.spring.components.stats.stats.ScheduledStats;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class NettyUtils {

  public static boolean isEpollAvailable() {
    return Epoll.isAvailable();
  }

  public static Class<? extends SocketChannel> socketChannel() {
    return isEpollAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
  }

  public static Class<? extends ServerSocketChannel> serverSocketChannel() {
    return isEpollAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
  }

  public static EventLoopGroup eventLoopGroup(String name, int threads, boolean daemon) {
    MultithreadEventLoopGroup loopGroup;
    if (isEpollAvailable()) {
      loopGroup = new EpollEventLoopGroup(threads, new DefaultThreadFactory(name, daemon));
    } else {
      loopGroup = new NioEventLoopGroup(threads, new DefaultThreadFactory(name, daemon));
    }
    monitor(name, loopGroup);
    return loopGroup;
  }

  private static void monitor(String name, MultithreadEventLoopGroup group) {
    WeakReference<MultithreadEventLoopGroup> reference = new WeakReference<>(group);
    ScheduledStats.scheduleWithFixedDelay(
        () -> {
          MultithreadEventLoopGroup loopGroup = reference.get();
          if (loopGroup == null) {
            throw new IllegalStateException("reference is null");
          }
          int waiting = 0;
          for (EventExecutor executor : loopGroup) {
            if (executor instanceof SingleThreadEventExecutor) {
              waiting += ((SingleThreadEventExecutor) executor).pendingTasks();
            }
          }
          ThreadPool.POOL_STATE_ACTIVE.set(loopGroup.executorCount(), name);
          ThreadPool.POOL_STATE_TASK_WAITING.set(waiting, name);
        },
        1,
        1,
        TimeUnit.SECONDS
    );
  }

}
