package application.component

import javax.inject.Inject

import scala.collection.immutable

private[application] class ServiceRegistry[A] @Inject() (implementations: immutable.Map[String, A], settings: ServiceRegistry.Settings[A]) {

  private val services: Map[String, A] = implementations
  private var activeServices: Set[String] = Set[String]()

  def getRegistered: Vector[String] = services.keys.toVector

  def hasActive: Boolean = activeServices.nonEmpty

  def isActive(name: String): Boolean = activeServices.contains(name)

  def allowsMultiple: Boolean = settings.allowsMultiple

  def alsoActivate(name: String): Boolean = {
    if (settings.allowsMultiple && services.contains(name)) {
      activeServices += name
      true
    } else false
  }

  def onlyActivate(name: String): Boolean = {
    if (services.contains(name)) {
      activeServices = Set[String](name)
      true
    } else false
  }

  def deactivateOne(name: String): Boolean = {
    if (settings.allowsMultiple && activeServices.contains(name)) {
      activeServices -= name
      true
    } else false
  }

  def deactivateAll(): Boolean = {
    activeServices = Set[String]()
    true
  }

  def use(f: A => Unit): Unit = activeServices.flatMap(service => services.get(service)).foreach(f)

}

object ServiceRegistry {

  case class Settings[A](allowsMultiple: Boolean)

}