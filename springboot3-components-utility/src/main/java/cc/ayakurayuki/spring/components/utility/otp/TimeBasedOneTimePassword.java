package cc.ayakurayuki.spring.components.utility.otp;

import cc.ayakurayuki.spring.components.utility.strings.RandomStrings;
import com.google.common.io.BaseEncoding;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Time-based One-Time Password, a.k.a. TOTP
 *
 * @author Ayakura Yuki
 */
public final class TimeBasedOneTimePassword {

  // key byte length
  private static final int SECRET_SIZE = 10;

  // random algorithm
  private static final String RANDOM_ALGORITHM = "SHA1PRNG";

  // mac algorithm
  private static final String MAC_ALGORITHM = "HmacSHA1";

  // window shift seconds
  private static final int WINDOW_SIZE = 5;

  /**
   * Generate a random secret for comparing OTP code, which should be saved in both
   * server-side and client-side.
   */
  public static String generateSecret() throws GeneralSecurityException {
    String seed = RandomStrings.random(76, RandomStrings.LowerUpperDigit);
    SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_ALGORITHM);
    secureRandom.setSeed(Base64.getDecoder().decode(seed));
    byte[] seedBytes = secureRandom.generateSeed(SECRET_SIZE);
    return BaseEncoding.base32().encode(seedBytes);
  }

  /**
   * Create an opt auth link for Authenticator
   *
   * @param user   user identifier
   * @param secret generated random secret
   */
  public static String url(String user, String secret) {
    return "otpauth://totp/%s?secret=%s".formatted(user, secret);
  }

  /**
   * Verify the given code with current timestamp millis in server
   *
   * @param secret user's secret
   * @param code   the given code from user's Authenticator
   */
  public static boolean verify(String secret, String code) {
    return verify(secret, code, System.currentTimeMillis());
  }

  /**
   * Verify the given code
   *
   * @param secret user's secret
   * @param code   the given code from user's Authenticator
   * @param millis timestamp millis
   */
  public static boolean verify(String secret, String code, long millis) {
    byte[] decodedKey = BaseEncoding.base32().decode(secret);
    // convert timestamp millis into 30-second window period, this is the advice from TOTP standard
    long t = (millis / 1000L) / 30L;
    // Window period is used to check codes generated in the near past.
    // You can use this value to tune how far you're willing to go.
    for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
      long hash;
      try {
        hash = verify(decodedKey, t + i);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      if (Long.valueOf(hash).toString().equals(code)) {
        return true; // acceptable code
      }
    }
    return false; // incorrect code
  }

  private static int verify(byte[] key, long t) throws GeneralSecurityException {
    byte[] data = new byte[8];
    long value = t;
    for (int i = 8; i-- > 0; value >>>= 8) {
      data[i] = (byte) value;
    }
    Mac mac = Mac.getInstance(MAC_ALGORITHM);
    SecretKeySpec signKey = new SecretKeySpec(key, MAC_ALGORITHM);
    mac.init(signKey);
    byte[] hash = mac.doFinal(data);
    int offset = hash[20 - 1] & 0xF;
    // oops, java does not have unsigned long
    long truncatedHash = 0;
    for (int i = 0; i < 4; ++i) {
      truncatedHash <<= 8;
      truncatedHash |= (hash[offset + i] & 0xFF);
    }
    truncatedHash &= 0x7FFFFFFF;
    truncatedHash %= 1000000;
    return (int) truncatedHash;
  }

}
