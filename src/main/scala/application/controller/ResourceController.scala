package application.controller

import application.component.ResourceManager
import javax.inject.Inject

trait ResourceController {

  def getAll: Vector[String]

}

private[application] class ResourceControllerImpl @Inject() (resourceManager: ResourceManager) extends ResourceController {

  def getAll: Vector[String] = resourceManager.getRegisteredResources

}
