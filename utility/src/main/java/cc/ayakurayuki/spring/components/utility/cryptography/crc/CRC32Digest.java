package cc.ayakurayuki.spring.components.utility.cryptography.crc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public abstract class CRC32Digest {

  public static long digest(byte[] src) {
    CRC32 crc32 = new CRC32();
    crc32.update(src);
    return crc32.getValue();
  }

  public static long digest(String src) {
    return digest(src, StandardCharsets.UTF_8);
  }

  public static long digest(String src, Charset charset) {
    CRC32 crc32 = new CRC32();
    crc32.update(src.getBytes(charset));
    return crc32.getValue();
  }

}
