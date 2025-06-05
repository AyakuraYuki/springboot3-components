package cc.ayakurayuki.spring.components.utility.dataurl.encoder;

import java.io.Serial;

/**
 * URL Encoded implementation of {@link IEncoder}
 *
 * @author Max Schuster
 */
public class URLEncodedEncoder implements IEncoder {

  @Serial
  private static final long serialVersionUID = -6700265818521321087L;

  @Override
  public byte[] decode(String charset, String string) throws Exception {
    return URLEncoding.decode(string, charset).getBytes(charset);
  }

  @Override
  public String encode(String charset, byte[] data) throws Exception {
    return URLEncoding.encode(new String(data, charset), charset);
  }

}
