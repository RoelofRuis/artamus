package logging

import core.components.Logger
import net.codingwell.scalaguice.{ScalaMapBinder, ScalaModule}

class LoggingModule extends ScalaModule {

  override def configure(): Unit = {
    val loggers = ScalaMapBinder.newMapBinder[String, Logger](binder)
    loggers.addBinding("printing-logger").to[PrintingLogger]
  }

}
