package cc.ayakurayuki.spring.components.starter.http.server;

import cc.ayakurayuki.spring.components.context.Context;

public class HttpExceptions {

  @SuppressWarnings("rawtypes")
  public static void attachContextException(Throwable e) {
    Context context = Context.current();
    context.setException(e);
  }

}
