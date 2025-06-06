package cc.ayakurayuki.spring.components.context;

import java.util.Map;
import javax.annotation.Nonnull;

/**
 * @author Yann
 */
public interface User {

  long getId();

  @Nonnull
  Map<String, String> logMetadata();

}
