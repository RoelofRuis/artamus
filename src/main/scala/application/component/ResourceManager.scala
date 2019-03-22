package application.component

import application.ports.ManagedResource
import javax.inject.Inject

import scala.collection.immutable

/**
  * @deprecated Drivers should manage their own resources
  */
private[application] class ResourceManager @Inject() (resources: immutable.Set[ManagedResource], logger: Logger) {

  def getRegisteredResources: Vector[String] = resources.map(_.getDescription).toVector

  def closeAll(): Unit = resources.foreach{ res =>
    logger.debug(s"Closing [${res.getName}]")
    res.close()
  }
}
