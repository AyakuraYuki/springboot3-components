package cc.ayakurayuki.spring.components.starter.rpc.server;

import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class RPCServicesRegistry implements ApplicationListener<ApplicationEvent>, RPCServiceRegistry {

  private final BindableService reflectionService = ProtoReflectionServiceV1.newInstance();

  @Override
  public void onApplicationEvent(@Nonnull ApplicationEvent event) {
    // do nothing
  }

  @Override
  public Collection<RPCServiceDefinition> definedServices() {
    return Collections.singleton(
        new RPCServiceDefinition(
            this.reflectionService.getClass(),
            this.reflectionService,
            this.reflectionService.bindService()
        )
    );
  }

}
