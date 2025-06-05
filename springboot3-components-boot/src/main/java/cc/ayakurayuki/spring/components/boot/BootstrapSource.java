package cc.ayakurayuki.spring.components.boot;

/**
 * BootstrapSource enables class preloading during program startup by scanning specified packages
 * to load and warm up all classes within them.
 * <p>
 * The preloading implementation leverages the Java SPI (Service Provider Interface) mechanism.
 * It discovers all configuration entries by scanning for implementation classes of the
 * BootstrapSource interface.
 *
 * @author Ayakura Yuki
 */
public interface BootstrapSource {

  /**
   * Specifies packages that should be excluded from preloading.
   * By default, returns an empty array indicating no packages are excluded.
   */
  default String[] exclude() {
    return new String[0];
  }

  /**
   * Specifies the list of packages to scan for class preloading.
   * Define package granularity according to your needs.
   * <p>
   * For example, to load two specific packages:
   * <ul>
   *   <li>{@code com.example.util}</li>
   *   <li>{@code com.example.const}</li>
   * </ul>
   * provide their full package names.
   * <p>
   * If specifying a parent package like {@code com.example}, all its subpackages will be included.
   * In such cases, you may need to use {@link #exclude()} to filter out unwanted subpackages.
   */
  String[] packages();

}
