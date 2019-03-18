package application.controller

import application.component.ServiceRegistry
import javax.inject.Inject

trait ServiceControllerOld[A] {

  /**
    * @return Vector holding a tuple: the available service names and whether the service is active.
    */
  def getAll: Vector[(String, Boolean)]

  /**
    * @return Whether the service has at least one active instance
    */
  def hasActive: Boolean

  /**
    * @return Whether deactivating succeeded
    */
  def deactivateAll: Boolean

  /**
    * @param string The service name
    * @return Whether toggling the service succeeded
    */
  def toggle(string: String): Boolean

  /**
    * @return Whether the service allows multiple implementations to be selected simultaneously
    */
  def allowsMultiple: Boolean

}

private[application] class ServiceControllerOldImpl[A] @Inject() (registry: ServiceRegistry[A]) extends ServiceControllerOld[A] {

  def getAll: Vector[(String, Boolean)] = {
    registry.getRegistered.map(name => (name, registry.isActive(name)))
  }

  def hasActive: Boolean = registry.hasActive

  def toggle(string: String): Boolean = {
    if (registry.allowsMultiple) {
      if (registry.isActive(string)) registry.deactivateOne(string)
      else registry.alsoActivate(string)
    } else {
      if (registry.isActive(string)) registry.deactivateAll()
      else registry.onlyActivate(string)
    }
  }

  def deactivateAll: Boolean = {
    registry.deactivateAll()
    true
  }

  def allowsMultiple: Boolean = registry.allowsMultiple

}
