package core.application

import com.google.inject.Inject

import scala.collection.immutable

trait Registerable {
  def id: String
  def default: Boolean
}

class ServiceRegistry[A] @Inject() (implementations: immutable.Set[A with Registerable]) {

  private var services: Map[String, A] = implementations.map(service => (service.id, service)).toMap
  private var activeService: String = implementations.find(_.default).getOrElse {
    throw new IllegalStateException(s"ServiceRegistry has no default implementation bound")
  }.id

  def register(name: String, implementation: A): Unit = services += (name -> implementation)

  def getAvailableServices: Vector[(String, A)] = services.toVector

  def makeActive(name: String): Boolean = {
    if (services.contains(name)) {
      activeService = name
      true
    } else false
  }

  def getActiveName: String = activeService

  def getActive: A = services(activeService)

}
