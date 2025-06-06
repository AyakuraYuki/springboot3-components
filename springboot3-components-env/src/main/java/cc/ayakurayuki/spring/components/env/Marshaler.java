package cc.ayakurayuki.spring.components.env;

/**
 * Marshaler for environment value that are parse into raw value
 */
public interface Marshaler<V> {

  V parse(String value);

}
