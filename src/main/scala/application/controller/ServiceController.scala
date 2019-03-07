package application.controller

import application.component.ServiceRegistry
import javax.inject.Inject

class ServiceController[A] @Inject() (registry: ServiceRegistry[A]) {

  /**
    * @return Vector holding a tuple: the available service names and whether the service is active.
    */
  def getAll: Vector[(String, Boolean)] = {
    registry.getRegistered.map { case (name, _) => (name, registry.getActive.contains(name)) }
  }

  /**
    * @return Whether the service has at least one active instance
    */
  def hasActive: Boolean = registry.getActive.isDefined

  /**
    * @return Whether deactivating succeeded
    */
  def deactivateAll: Boolean = {
    registry.deactivate()
    true
  }

  /**
    * @param string The service name
    * @return Whether activating the given service succeeded
    */
  def activateOne(string: String): Boolean = registry.setActive(string)

}
