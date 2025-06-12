package cc.ayakurayuki.spring.components.starter.rpc.server;

import cc.ayakurayuki.spring.components.rpc.server.RpcServerIndicator;
import cc.ayakurayuki.spring.components.rpc.server.additional.MethodAnnotations;
import cc.ayakurayuki.spring.components.utility.collection.CollectionUtils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.netty.channel.Channel;
import java.util.List;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Configurable
@ConditionalOnClass({Server.class, ServerBuilder.class, RpcServerIndicator.class})
@EnableConfigurationProperties(RPCServiceProperties.class)
public class RPCServiceAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RPCServiceBeanDiscoverer rpcServiceBeanDiscoverer() {
    return new RPCServiceBeanDiscoverer();
  }

  @Bean
  @ConditionalOnMissingBean
  public RPCServicesRegistry rpcServicesRegistry() {
    return new RPCServicesRegistry();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnClass(Channel.class)
  public RPCServiceServer rpcServiceServer(List<RPCServiceRegistry> registries, RPCServiceProperties properties) {
    RPCServiceServer serviceServer = new RPCServiceServer(properties);
    if (CollectionUtils.isEmpty(registries)) {
      return serviceServer;
    }
    for (RPCServiceRegistry registry : registries) {
      for (RPCServiceDefinition service : registry.definedServices()) {
        serviceServer.addService(service);
        serviceServer.addMethodAnnotations(service);
      }
    }
    return serviceServer;
  }

  @Bean
  @ConditionalOnMissingBean
  public RPCServiceLifecycle rpcServiceLifecycle(RPCServiceServer rpcServiceServer) {
    return new RPCServiceLifecycle(rpcServiceServer);
  }

  @Bean
  @ConditionalOnMissingBean
  public MethodAnnotations methodAnnotations(RPCServiceServer rpcServiceServer) {
    return rpcServiceServer.getMethodAnnotations();
  }

}
