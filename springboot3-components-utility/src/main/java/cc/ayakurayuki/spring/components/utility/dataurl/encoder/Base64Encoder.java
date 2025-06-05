package cc.ayakurayuki.spring.components.utility.dataurl.encoder;

import java.io.Serial;
import java.util.Base64;

/**
 * Base64 implementation of {@link IEncoder}
 *
 * @author Max Schuster
 */
public class Base64Encoder implements IEncoder {

  @Serial
  private static final long serialVersionUID = 4669788332992550965L;

  @Override
  public byte[] decode(String charset, String string) throws Exception {
    return Base64.getDecoder().decode(string);
  }

  @Override
  public String encode(String charset, byte[] data) throws Exception {
    return new String(Base64.getEncoder().encode(data));
  }

}
