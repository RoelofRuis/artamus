package core

import com.google.inject.Inject
import core.components.Logger

import scala.collection.mutable.ListBuffer

class ResourceManager @Inject() (logger: Logger) {

  private val hooks = ListBuffer[(String, () => Unit)]()

  def registerOnShutdown(name: String, closeHook: () => Unit): Unit = {
    logger.debug(s"Registering close hook [$name]")
    hooks.append((name, closeHook))
  }

  def closeAll(): Unit = {
    logger.debug(s"Closing [${hooks.size}] resources")
    hooks.foreach{ case (name, hook) =>
      logger.debug(s"Closing $name")
      hook()
    }
  }

}
