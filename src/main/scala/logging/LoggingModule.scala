package logging

import core.components.Logger
import net.codingwell.scalaguice.ScalaModule

class LoggingModule extends ScalaModule {

  override def configure(): Unit = {
    bind[Logger].to[PrintingLogger]
  }

}
