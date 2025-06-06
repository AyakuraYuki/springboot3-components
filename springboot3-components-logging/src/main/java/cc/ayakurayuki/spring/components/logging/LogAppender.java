package cc.ayakurayuki.spring.components.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

public class LogAppender extends RollingFileAppender<ILoggingEvent> {

  @Override
  protected void subAppend(ILoggingEvent event) {
    super.subAppend(event);
    // todo
  }

}
