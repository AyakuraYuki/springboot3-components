package cc.ayakurayuki.spring.components.context;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Yann
 */
public record IP(
    String clientIP,
    String userIP,
    int userPort
) implements Serializable, Cloneable {

  @Serial
  private static final long serialVersionUID = -8772302812009733326L;

  @Override
  public IP clone() {
    final IP ip;
    try {
      ip = (IP) super.clone();
    } catch (CloneNotSupportedException e) {
      return new IP(clientIP, userIP, userPort);
    }
    return ip;
  }

}
