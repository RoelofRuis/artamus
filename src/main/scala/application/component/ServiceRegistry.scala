package application.component

import javax.inject.Inject

import scala.collection.immutable

class ServiceRegistry[A] @Inject() (implementations: immutable.Map[String, A]) {

  private var services: Map[String, A] = implementations
  private var activeService: Option[String] = None

  def register(name: String, implementation: A): Unit = services += (name -> implementation)

  def getRegistered: Vector[(String, A)] = services.toVector

  def setActive(name: String): Boolean = {
    if (services.contains(name)) {
      activeService = Some(name)
      true
    } else false
  }

  def deactivate(): Unit = {
    activeService = None
  }

  def getActive: Option[String] = activeService

  def use(f: A => Unit): Unit = activeService.flatMap(service => services.get(service)).foreach(f)

}
