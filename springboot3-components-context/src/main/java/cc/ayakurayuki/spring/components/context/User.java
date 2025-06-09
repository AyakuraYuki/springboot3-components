package cc.ayakurayuki.spring.components.context;

import jakarta.annotation.Nonnull;
import java.util.Map;

/**
 * @author Yann
 */
public interface User {

  long getId();

  @Nonnull
  Map<String, String> logMetadata();

}
