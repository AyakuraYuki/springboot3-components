package cc.ayakurayuki.spring.components.starter.http.server;

import java.time.Duration;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class TomcatWebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>, Ordered {

  private final TomcatHttpServerProperties properties;

  public TomcatWebServerCustomizer(TomcatHttpServerProperties properties) {
    this.properties = properties;
  }

  @Override
  public void customize(ConfigurableTomcatWebServerFactory factory) {
    factory.setPort(properties.getPort());
    customizeMinThreads(factory, properties.getMinThreads()); // minimum spare threads
    customizeMaxThreads(factory, properties.getMaxThreads()); // maximum threads
    customizeAcceptCount(factory, properties.getAcceptCount()); // wait queue accept count
    customizeMaxConnections(factory, properties.getMaxConnections()); // maximum connection
    customizeKeepAliveTimeout(factory, Duration.ofSeconds(20)); // keepalive timeout
    customizeConnectionTimeout(factory, Duration.ofSeconds(1)); // connection timeout
    customizeRelaxedQueryChars(factory, "|{}"); // http query characters
  }

  private void customizeMinThreads(ConfigurableTomcatWebServerFactory factory, int minSpareThreads) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractProtocol<?> protocol) {
        protocol.setMinSpareThreads(minSpareThreads);
      }
    });
  }

  private void customizeMaxThreads(ConfigurableTomcatWebServerFactory factory, int maxThreads) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractProtocol<?> protocol) {
        protocol.setMaxThreads(maxThreads);
      }
    });
  }

  private void customizeAcceptCount(ConfigurableTomcatWebServerFactory factory, int acceptCount) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractProtocol<?> protocol) {
        protocol.setAcceptCount(acceptCount);
      }
    });
  }

  private void customizeMaxConnections(ConfigurableTomcatWebServerFactory factory, int maxConnections) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractProtocol<?> protocol) {
        protocol.setMaxConnections(maxConnections);
      }
    });
  }

  private void customizeKeepAliveTimeout(ConfigurableTomcatWebServerFactory factory, Duration keepAliveTimeout) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractProtocol<?> protocol) {
        protocol.setKeepAliveTimeout((int) keepAliveTimeout.toMillis());
      }
    });
  }

  private void customizeConnectionTimeout(ConfigurableTomcatWebServerFactory factory, Duration connectionTimeout) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractProtocol<?> protocol) {
        protocol.setConnectionTimeout((int) connectionTimeout.toMillis());
      }
    });
  }

  private void customizeRelaxedQueryChars(ConfigurableTomcatWebServerFactory factory, String relaxedQueryChars) {
    factory.addConnectorCustomizers(connector -> {
      ProtocolHandler protocolHandler = connector.getProtocolHandler();
      if (protocolHandler instanceof AbstractHttp11Protocol<?> protocol) {
        protocol.setRelaxedQueryChars(relaxedQueryChars);
      }
    });
  }

  @Override
  public int getOrder() {
    return 0;
  }

}
