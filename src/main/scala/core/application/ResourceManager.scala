package core.application

import com.google.inject.Inject
import core.components.Logger

import scala.collection.mutable.ListBuffer

class ResourceManager @Inject() (logger: ServiceRegistry[Logger]) {

  private val hooks = ListBuffer[(String, () => Unit)]()

  def register(name: String, shutdownHook: () => Unit): Unit = {
    logger.getActive.debug(s"Registering [$name]")
    hooks.append((name, shutdownHook))
  }

  def closeAll(): Unit = {
    hooks.foreach{ case (name, shutdownHook) =>
      logger.getActive.debug(s"Closing $name")
      shutdownHook()
    }
  }

  def getRegisteredResources: Vector[String] = hooks.map(_._1).toVector

}
