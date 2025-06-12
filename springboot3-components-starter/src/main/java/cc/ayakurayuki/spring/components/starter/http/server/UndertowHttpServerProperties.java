package cc.ayakurayuki.spring.components.starter.http.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.http.server.undertow")
public class UndertowHttpServerProperties {

  /**
   * HTTP server listen port
   */
  private int port = 8081;

  /**
   * Undertow buffer size
   */
  private int bufferSize = 0;

  /**
   * Undertow io threads
   */
  private int ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);

  /**
   * Undertow worker threads
   */
  private int workerThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2) * 8;

}
