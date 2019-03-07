package application.component

import application.ports.ManagedResource
import com.google.inject.Inject

import scala.collection.immutable

class ResourceManager @Inject() (resources: immutable.Set[ManagedResource]) {

  def getRegisteredResources: Vector[String] = resources.map(_.getName).toVector

  def closeAll(): Unit = resources.foreach(_.close())

}
