package application.component

import application.ports.{Logger, ManagedResource}
import javax.inject.Inject

import scala.collection.immutable

private[application] class ResourceManager @Inject() (resources: immutable.Set[ManagedResource], logger: ServiceRegistry[Logger]) {

  def getRegisteredResources: Vector[String] = resources.map(_.getDescription).toVector

  def closeAll(): Unit = resources.foreach{ res =>
    logger.useAllActive(_.debug(s"Closing [${res.getName}]"))
    res.close()
  }
}
