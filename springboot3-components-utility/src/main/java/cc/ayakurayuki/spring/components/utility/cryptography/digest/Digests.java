package cc.ayakurayuki.spring.components.utility.cryptography.digest;

import cc.ayakurayuki.spring.components.utility.cryptography.DigestAlgorithm;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Ayakura Yuki
 */
public final class Digests {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public static byte[] randomSalt(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("length of bytes must be a positive number and greater than zero, you present %d".formatted(length));
    }
    byte[] salt = new byte[length];
    SECURE_RANDOM.nextBytes(salt);
    return salt;
  }

  // ----------------------------------------------------------------------------------------------------

  public static String digestToHexString(String src, DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
    return digestToHexString(src, StandardCharsets.UTF_8, algorithm);
  }

  public static String digestToHexString(String src, String algorithm) throws NoSuchAlgorithmException {
    return digestToHexString(src, StandardCharsets.UTF_8, algorithm);
  }

  public static String digestToHexString(String src, Charset charset, DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    return digestToHexString(src, charset, algorithm.algorithm);
  }

  public static String digestToHexString(String src, Charset charset, String algorithm) throws NoSuchAlgorithmException {
    byte[] dst = digest(src, charset, algorithm);
    if (dst == null) {
      return null;
    }
    return BaseEncoding.base16().encode(dst);
  }

  // ----------------------------------------------------------------------------------------------------

  public static byte[] digest(String src, DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
    return digest(src, StandardCharsets.UTF_8, algorithm);
  }

  public static byte[] digest(String src, String algorithm) throws NoSuchAlgorithmException {
    return digest(src, StandardCharsets.UTF_8, algorithm);
  }

  public static byte[] digest(String src, Charset charset, DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    return digest(src, charset, algorithm.algorithm);
  }

  public static byte[] digest(String src, Charset charset, String algorithm) throws NoSuchAlgorithmException {
    if (src == null) {
      return null;
    }
    if (charset == null) {
      charset = StandardCharsets.UTF_8;
    }
    byte[] srcBytes = src.getBytes(charset);
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    return messageDigest.digest(srcBytes);
  }

  public static byte[] digest(byte[] src, DigestAlgorithm algorithm, byte[] salt) throws NoSuchAlgorithmException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    return digest(src, algorithm.algorithm, salt);
  }

  public static byte[] digest(byte[] src, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
    if (src == null) {
      return null;
    }
    MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
    if (salt != null && salt.length > 0) {
      messageDigest.update(salt);
    }
    return messageDigest.digest(src);
  }

  // ----------------------------------------------------------------------------------------------------

  public static byte[] digest(InputStream src, DigestAlgorithm algorithm) throws NoSuchAlgorithmException, IOException {
    if (algorithm == null) {
      throw new IllegalArgumentException("algorithm cannot be null");
    }
    return digest(src, algorithm.algorithm);
  }

  public static byte[] digest(InputStream src, String algorithm) throws NoSuchAlgorithmException, IOException {
    if (src == null) {
      return null;
    }
    MessageDigest digest = MessageDigest.getInstance(algorithm);
    int bufferSize = 8 * 1024;
    byte[] buffer = new byte[bufferSize];
    int read = src.read(buffer, 0, bufferSize);
    while (read > -1) {
      digest.update(buffer, 0, read);
      read = src.read(buffer, 0, bufferSize);
    }
    return digest.digest();
  }

}
