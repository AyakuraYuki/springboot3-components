package cc.ayakurayuki.spring.components.env;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Environments {

  public static final Marshaler<String> STRING_MARSHALLER = value -> value;

  public static final Marshaler<Integer> INT_MARSHALLER = Ints::tryParse;

  public static final Marshaler<Long> LONG_MARSHALLER = Longs::tryParse;

  public static final Marshaler<Boolean> BOOLEAN_MARSHALLER = Boolean::parseBoolean;

  public static final String IP;

  public static final String HOSTNAME;

  static {
    String hostname = "";
    String address = "";
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      hostname = localHost.getHostName();
      address = localHost.getHostAddress();
    } catch (UnknownHostException ignored) {
      // nothing
    }
    IP = address;
    HOSTNAME = hostname;
  }

  private Environments() {
  }

}
