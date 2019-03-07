package application.controller

import application.component.ResourceManager
import javax.inject.Inject

class ResourceController @Inject() (resourceManager: ResourceManager) {

  def getAll: Vector[String] = resourceManager.getRegisteredResources

}
