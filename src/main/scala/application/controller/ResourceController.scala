package application.controller

import application.component.ResourceManager
import com.google.inject.Inject

class ResourceController @Inject() (resourceManager: ResourceManager) {

  def getAll: Vector[String] = resourceManager.getRegisteredResources

}
