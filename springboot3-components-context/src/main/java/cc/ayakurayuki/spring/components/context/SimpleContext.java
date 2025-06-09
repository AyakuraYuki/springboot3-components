package cc.ayakurayuki.spring.components.context;

import jakarta.annotation.Nonnull;

/**
 * @author Yann
 */
@SuppressWarnings("rawtypes")
public class SimpleContext extends Context {

  private SimpleContext(@Nonnull String title) {
    this.setPath(title);
  }

  public static SimpleContext create(@Nonnull String title) {
    return new SimpleContext(title);
  }

  /**
   * @see Context#detach(Context)
   */
  @Deprecated
  public void detach() {
    detach(null);
  }

}
