package cc.ayakurayuki.spring.components.context.user;

import cc.ayakurayuki.spring.components.context.User;
import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ayakura Yuki
 */
public record AuthUser(long userID, String token) implements User {

  @Override
  public long getId() {
    return this.userID();
  }

  @Nonnull
  @Override
  public Map<String, String> logMetadata() {
    Map<String, String> map = new HashMap<>(1);
    map.put("userID", String.valueOf(this.userID()));
    return map;
  }

}
