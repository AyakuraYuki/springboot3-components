package cc.ayakurayuki.spring.components.starter.rpc.server;

import java.util.Collection;

public interface RPCServiceRegistry {

  Collection<RPCServiceDefinition> definedServices();

}
