package logging

import core.application.Registerable
import core.components.Logger
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class LoggingModule extends ScalaModule {

  override def configure(): Unit = {
    val loggers = ScalaMultibinder.newSetBinder[Logger with Registerable](binder)
    loggers.addBinding.to[PrintingLogger]
  }

}
