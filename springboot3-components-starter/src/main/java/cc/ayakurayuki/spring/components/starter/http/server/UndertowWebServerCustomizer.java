package cc.ayakurayuki.spring.components.starter.http.server;

import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class UndertowWebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableUndertowWebServerFactory>, Ordered {

  private final UndertowHttpServerProperties properties;

  public UndertowWebServerCustomizer(UndertowHttpServerProperties properties) {
    this.properties = properties;
  }

  @Override
  public void customize(ConfigurableUndertowWebServerFactory factory) {
    factory.setPort(properties.getPort());

    if (properties.getBufferSize() > 0) {
      factory.setBufferSize(properties.getBufferSize());
    }

    if (properties.getIoThreads() > 0) {
      factory.setIoThreads(properties.getIoThreads());
    }

    if (properties.getWorkerThreads() > 0) {
      factory.setWorkerThreads(properties.getWorkerThreads());
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }

}
