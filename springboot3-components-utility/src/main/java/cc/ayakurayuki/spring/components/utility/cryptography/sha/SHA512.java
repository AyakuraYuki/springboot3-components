package cc.ayakurayuki.spring.components.utility.cryptography.sha;

import cc.ayakurayuki.spring.components.utility.cryptography.DigestAlgorithm;
import cc.ayakurayuki.spring.components.utility.cryptography.digest.Digests;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * @author Ayakura Yuki
 */
public final class SHA512 {

  /**
   * 对输入的 bytes 进行 sha512 散列
   */
  public static byte[] digest(byte[] input) throws NoSuchAlgorithmException {
    return Digests.digest(input, DigestAlgorithm.SHA512, null);
  }

  /**
   * 对输入的 bytes 进行 sha512 散列，带 salt 达到更高的安全性
   */
  public static byte[] digest(byte[] input, byte[] salt) throws NoSuchAlgorithmException {
    return Digests.digest(input, DigestAlgorithm.SHA512, salt);
  }

  /**
   * 对输入的字符串进行 sha512 散列
   */
  public static byte[] digest(String input) throws NoSuchAlgorithmException {
    return Digests.digest(input.getBytes(StandardCharsets.UTF_8), DigestAlgorithm.SHA512, null);
  }

  /**
   * 对输入的字符串进行 sha512 散列
   */
  public static byte[] digest(String input, Charset charset) throws NoSuchAlgorithmException {
    return Digests.digest(input.getBytes(charset), DigestAlgorithm.SHA512, null);
  }

  /**
   * 对输入的字符串进行 sha512 散列，带 salt 达到更高的安全性
   */
  public static byte[] digest(String input, byte[] salt) throws NoSuchAlgorithmException {
    return Digests.digest(input.getBytes(StandardCharsets.UTF_8), DigestAlgorithm.SHA512, salt);
  }

  /**
   * 对输入的字符串进行 sha512 散列，带 salt 达到更高的安全性
   */
  public static byte[] digest(String input, Charset charset, byte[] salt) throws NoSuchAlgorithmException {
    return Digests.digest(input.getBytes(charset), DigestAlgorithm.SHA512, salt);
  }

  /**
   * 对文件进行 sha512 散列
   */
  public static byte[] digest(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
    return Digests.digest(inputStream, DigestAlgorithm.SHA512);
  }

}
