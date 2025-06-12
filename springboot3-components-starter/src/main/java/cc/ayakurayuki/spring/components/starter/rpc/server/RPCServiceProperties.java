package cc.ayakurayuki.spring.components.starter.rpc.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.rpc.server")
@Getter
@Setter
public class RPCServiceProperties {

  /**
   * Sets listen port
   */
  private int port = 9001;

  /**
   * Sets message compressor name
   */
  private String compressor = "identity";

  /**
   * Sets the HTTP/2 flow control window. The default value is 8m
   */
  private int flowControlWindow = 1024 * 1024 * 8;

  /**
   * Sets the maximum size of metadata allowed to be received. The default is 8 KiB.
   */
  private int maxInboundMetadataSize = 8192;

  /**
   * Sets the maximum message size allowed for a single gRPC frame. Defaults to 4 MiB
   */
  private int maxInboundMessageSize = 4 * 1024 * 1024;

}
