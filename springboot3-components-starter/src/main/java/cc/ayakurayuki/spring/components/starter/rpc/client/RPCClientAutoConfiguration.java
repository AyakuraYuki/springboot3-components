package cc.ayakurayuki.spring.components.starter.rpc.client;

import cc.ayakurayuki.spring.components.rpc.client.RpcClientIndicator;
import io.grpc.Channel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Channel.class, RpcClientIndicator.class})
@EnableConfigurationProperties(RPCChannelsProperties.class)
public class RPCClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RPCNamingClientChannelFactory rpcNamingClientChannelFactory(RPCChannelsProperties properties) {
    return new RPCNamingClientChannelFactory(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  public RPCClientBeanPostProcessor rpcClientBeanPostProcessor(RPCNamingClientChannelFactory rpcNamingClientChannelFactory) {
    return new RPCClientBeanPostProcessor(rpcNamingClientChannelFactory);
  }

}
