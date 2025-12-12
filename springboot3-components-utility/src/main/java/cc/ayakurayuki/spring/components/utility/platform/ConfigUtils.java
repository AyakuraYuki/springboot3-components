package cc.ayakurayuki.spring.components.utility.platform;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author Ayakura Yuki
 * @date 2025/12/12-09:04
 */
public final class ConfigUtils {

    private ConfigUtils() {}

    /// find the environment variable, or use default value
    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null ? fallback : value;
    }

    /// find the first exists path, otherwise return null
    private static String firstExists(File... paths) {
        for (File path : paths) {
            if (path.exists()) {
                return path.getAbsolutePath();
            }
        }
        return null;
    }

    public static String userDir(@Nonnull String mainDirectoryName) {
        String userDir = System.getProperty("user.dir");
        File pluginPath = new File(userDir);
        if (new File(pluginPath, "bin").exists()) {
            return pluginPath.getAbsolutePath();
        } else if (pluginPath.exists() && pluginPath.getName().equals("bin")) {
            return pluginPath.getParentFile().getAbsolutePath();
        } else {
            return firstExists(
                    new File(pluginPath, mainDirectoryName),
                    new File(pluginPath.getParentFile(), mainDirectoryName)
            );
        }
    }

    public static synchronized void injectEnvironmentVariablesToProperties(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            String trimmed = value.trim();
            if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
                int begin = value.indexOf(":");
                if (begin < 0) {
                    begin = value.length() - 1;
                }
                int end = value.length() - 1;
                String envKey = value.substring(2, begin);
                String envValue = System.getenv(envKey);
                if (envValue == null || envValue.trim().isEmpty()) {
                    value = value.substring(begin + 1, end);
                } else {
                    value = envValue;
                }
                properties.setProperty(key, value);
            }
        }
    }

}
