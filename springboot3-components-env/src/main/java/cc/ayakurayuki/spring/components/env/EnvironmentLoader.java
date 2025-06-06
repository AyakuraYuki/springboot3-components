package cc.ayakurayuki.spring.components.env;

import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentLoader {

  static final String ENV_FILE = "env.properties";

  private static final Properties LOCAL_PROPERTIES = loadLocal();

  static Properties loadLocal() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL url = classLoader.getResource(ENV_FILE);
    if (url == null || url.getPath() == null) {
      log.info("local environment properties file %s is not exist".formatted(ENV_FILE));
      return null;
    }
    File file = new File(url.getPath());
    if (file.exists() && file.canRead()) {
      try {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
          props.load(fis);
        }
        return props;
      } catch (IOException e) {
        log.warn("local environment properties file %s load error".formatted(ENV_FILE), e);
      }
    }
    return null;
  }

  static <T> EnvironmentValueHolder<T> resolveEnvironment(Key<T> key) {
    T value = key.get(resolveValue(key));
    if (value != null) {
      return EnvironmentValueHolder.of(key.name(), value);
    }
    Key<T> fallbackKey = key.fallback();
    if (fallbackKey != null) {
      return resolveEnvironment(fallbackKey);
    }
    return EnvironmentValueHolder.of(key.name(), null);
  }

  static <T> String resolveValue(Key<T> key) {
    String name = key.name();
    String value = System.getenv(name.toUpperCase());
    if (Strings.isNullOrEmpty(value)) {
      value = System.getProperty(name);
    }
    if (LOCAL_PROPERTIES != null && Strings.isNullOrEmpty(value)) {
      value = LOCAL_PROPERTIES.getProperty(name);
    }
    return value;
  }

}
