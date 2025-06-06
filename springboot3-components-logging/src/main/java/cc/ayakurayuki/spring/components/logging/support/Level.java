package cc.ayakurayuki.spring.components.logging.support;

public enum Level {

  /**
   * NONE is a special level that can be used to turn off logging
   */
  NONE(LevelInt.NONE_INT, "NONE"),

  /**
   * BASIC is a most useful level for informational messages.
   * <p>
   * Typically BASIC level records most of the information, eg: request path / request args / escaped time / response status and so on
   */
  BASIC(LevelInt.BASIC_INT, "BASIC"),

  /**
   * HEADERS is a message level providing tracing information.
   * <p>
   * In addition to all {@link #BASIC} features, include request headers / response headers
   */
  HEADERS(LevelInt.HEADERS_INT, "HEADERS"),

  /**
   * BODY is a message level providing more powerful tracing information.
   * <p>
   * In addition to all {@link #HEADERS} features, include request body / response body
   */
  BODY(LevelInt.BODY_INT, "BODY");

  private final int    levelInt;
  private final String name;

  Level(int levelInt, String name) {
    this.levelInt = levelInt;
    this.name = name;
  }

  public int toInt() {
    return levelInt;
  }

  /**
   * Returns the string representation of this Level.
   */
  public String toString() {
    return name;
  }

  public boolean isEnable(Level other) {
    return this.levelInt <= other.levelInt;
  }

}
