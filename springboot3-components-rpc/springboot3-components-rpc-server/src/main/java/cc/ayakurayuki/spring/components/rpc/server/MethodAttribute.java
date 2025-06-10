package cc.ayakurayuki.spring.components.rpc.server;

import java.util.List;
import java.util.Map;

public interface MethodAttribute<T> {

  void attachAttributes(String methodReference, List<T> values);

  Map<String, List<T>> getAttributes();

  List<T> getAttribute(String methodReference);

}
