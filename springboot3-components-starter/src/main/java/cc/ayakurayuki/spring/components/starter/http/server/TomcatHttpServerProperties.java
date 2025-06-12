package cc.ayakurayuki.spring.components.starter.http.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.http.server.tomcat")
public class TomcatHttpServerProperties {

  /**
   * HTTP server listen port
   */
  private int port = 8081;

  /**
   * HTTP server minimum execute threads
   */
  private int minThreads = Runtime.getRuntime().availableProcessors() * 2;

  /**
   * HTTP server maximum execute threads
   */
  private int maxThreads = Runtime.getRuntime().availableProcessors() * 50; // e.g.: (4 cores) * 50 = (200 threads)

  /**
   * HTTP server NIO accept count
   */
  private int acceptCount = 1000;

  /**
   * HTTP server maximum NIO connections
   */
  private int maxConnections = 10000;

}
