package cc.ayakurayuki.spring.components.http.server;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;

public class HttpServerBootstrap implements BootstrapSource {

  @Override
  public String[] packages() {
    return new String[]{
        "jakarta.servlet",
        "org.springframework.web",
        "org.springframework.validation"
    };
  }

}
