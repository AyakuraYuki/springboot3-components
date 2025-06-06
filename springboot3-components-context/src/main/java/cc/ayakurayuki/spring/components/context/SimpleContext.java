package cc.ayakurayuki.spring.components.context;

import javax.annotation.Nonnull;

/**
 * @author Yann
 */
@SuppressWarnings("rawtypes")
public class SimpleContext extends Context {

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

  private SimpleContext(@Nonnull String title) {
    this.setPath(title);
  }

}
