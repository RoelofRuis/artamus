package application.component

import application.ports.{Logger, ManagedResource}
import javax.inject.Inject

import scala.collection.immutable

class ResourceManager @Inject() (resources: immutable.Set[ManagedResource], logger: ServiceRegistry[Logger]) {

  def getRegisteredResources: Vector[String] = resources.map(_.getName).toVector

  def closeAll(): Unit = resources.foreach{ res =>
    logger.use(_.debug(s"Closing [${res.getName}]"))
    res.close()
  }
}
