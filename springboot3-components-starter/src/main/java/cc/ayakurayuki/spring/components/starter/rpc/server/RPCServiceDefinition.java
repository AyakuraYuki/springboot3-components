package cc.ayakurayuki.spring.components.starter.rpc.server;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import jakarta.annotation.Nonnull;

public record RPCServiceDefinition(
    Class<?> beanClass,
    BindableService service,
    ServerServiceDefinition definition
) {

  public RPCServiceDefinition(@Nonnull Class<?> beanClass, @Nonnull BindableService service, @Nonnull ServerServiceDefinition definition) {
    this.beanClass = beanClass;
    this.service = service;
    this.definition = definition;
  }

}
