package cc.ayakurayuki.spring.components.utility.cryptography.hmac;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class Hmac {

  private static final String HmacSHA1   = "HmacSHA1";
  private static final String HmacSHA256 = "HmacSHA256";

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static String hmacSHA1(String data, String key) throws GeneralSecurityException {
    return hmac(data, key, HmacSHA1);
  }

  public static String hmacSHA1WithURLSafe(String data, String key) throws GeneralSecurityException {
    return hmacWithURLSafe(data, key, HmacSHA1);
  }

  public static String hmacSHA256(String data, String key) throws GeneralSecurityException {
    return hmac(data, key, HmacSHA256);
  }

  public static String hmacSHA256WithURLSafe(String data, String key) throws GeneralSecurityException {
    return hmacWithURLSafe(data, key, HmacSHA256);
  }

  private static String hmac(String data, String key, String algorithm) throws GeneralSecurityException {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
      Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
      mac.init(secretKeySpec);
      byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return new String(Base64.getEncoder().encode(hmac));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new SignatureException("Failed to get hmac.", e);
    }
  }

  private static String hmacWithURLSafe(String data, String key, String algorithm) throws GeneralSecurityException {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
      Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
      mac.init(secretKeySpec);
      byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return new String(Base64.getUrlEncoder().encode(hmac));
    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException e) {
      throw new SignatureException("Failed to get hmac.", e);
    }
  }

}
