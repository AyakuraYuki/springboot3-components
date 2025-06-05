package cc.ayakurayuki.spring.components.utility.snowflake;

/**
 * Snowflake id generator
 *
 * @author Ayakura Yuki
 */
public interface Snowflake {

  /**
   * obtain next snowflake id
   */
  long next();

}
