package application.controller

import application.component.ServiceRegistry
import javax.inject.Inject

trait ServiceController[A] {

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
    * @return Whether activating the given service succeeded
    */
  def activateOne(string: String): Boolean

}

private[application] class ServiceControllerImpl[A] @Inject() (registry: ServiceRegistry[A]) extends ServiceController[A] {

  def getAll: Vector[(String, Boolean)] = {
    registry.getRegistered.map { case (name, _) => (name, registry.getActive.contains(name)) }
  }

  def hasActive: Boolean = registry.getActive.isDefined

  def deactivateAll: Boolean = {
    registry.deactivate()
    true
  }

  def activateOne(string: String): Boolean = registry.setActive(string)

}
