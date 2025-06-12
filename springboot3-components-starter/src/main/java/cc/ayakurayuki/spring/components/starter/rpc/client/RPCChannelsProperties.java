package cc.ayakurayuki.spring.components.starter.rpc.client;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.rpc.client")
@Getter
@Setter
public class RPCChannelsProperties {

  private Map<String, RPCChannelProperties> client = new HashMap<>();

  public RPCChannelProperties getChannel(String name) {
    RPCChannelProperties properties = this.client.get(name);
    if (properties == null) {
      properties = RPCChannelProperties.DEFAULT;
    }
    return properties;
  }

}
