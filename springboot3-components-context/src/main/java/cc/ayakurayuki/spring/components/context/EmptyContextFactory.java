package cc.ayakurayuki.spring.components.context;

/**
 * empty context factory for avoiding class loading deadlock
 *
 * @author Ayakura Yuki
 */
class EmptyContextFactory {

  @SuppressWarnings("rawtypes")
  private static final Context EMPTY = new EmptyContext();

  @SuppressWarnings("unchecked")
  static <PK, PV, MK, MV> Context<PK, PV, MK, MV> create() {
    return EMPTY;
  }

  private static class EmptyContext extends Context<Void, Void, Void, Void> {}

}
