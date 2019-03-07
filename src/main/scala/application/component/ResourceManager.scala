package application.component

import application.ports.Logger
import com.google.inject.Inject

import scala.collection.mutable.ListBuffer

class ResourceManager @Inject() (logger: ServiceRegistry[Logger]) {

  private val hooks = ListBuffer[(String, () => Unit)]()

  def register(name: String, shutdownHook: () => Unit): Unit = {
    logger.use(_.debug(s"Registering [$name]"))
    hooks.append((name, shutdownHook))
  }

  def closeAll(): Unit = {
    hooks.foreach{ case (name, shutdownHook) =>
      logger.use(_.debug(s"Closing $name"))
      shutdownHook()
    }
  }

  def getRegisteredResources: Vector[String] = hooks.map(_._1).toVector

}
