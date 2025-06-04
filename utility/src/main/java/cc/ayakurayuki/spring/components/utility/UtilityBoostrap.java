package cc.ayakurayuki.spring.components.utility;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;

/**
 * @author Ayakura Yuki
 * @date 2025/06/04-16:11
 */
public class UtilityBoostrap implements BootstrapSource {

  @Override
  public String[] packages() {
    return new String[]{
        "org.apache.commons.lang3",
        "com.google.common",
        "com.google.gson",
        "org.bouncycastle"
    };
  }

}
