package cc.ayakurayuki.spring.components.rpc.server.additional;

import cc.ayakurayuki.spring.components.rpc.server.MethodAttribute;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodAnnotations implements MethodAttribute<Annotation> {

  private final Map<String, List<Annotation>> annotations = new HashMap<>(); // non-threads-safe

  @Override
  public void attachAttributes(String methodReference, List<Annotation> values) {
    this.annotations.put(methodReference, values);
  }

  @Override
  public Map<String, List<Annotation>> getAttributes() {
    return this.annotations;
  }

  @Override
  public List<Annotation> getAttribute(String methodReference) {
    return this.annotations.get(methodReference);
  }

}
