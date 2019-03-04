package core.application

import com.google.inject.Inject

import scala.collection.immutable

case class DefaultServiceName[A](name: String)

// TODO: Remove dependence on default service!
class ServiceRegistry[A] @Inject() (defaultService: DefaultServiceName[A], implementations: immutable.Map[String, A]) {

  private var services: Map[String, A] = implementations
  private var activeService: String = implementations.find(_._1 == defaultService.name).getOrElse {
    throw new IllegalStateException(s"ServiceRegistry has no default implementation bound")
  }._1

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
