package application.controller

import application.command.Command
import application.command.ResourceCommand.GetAllResources
import application.component.ResourceManager
import javax.inject.Inject

import scala.util.{Success, Try}

private[application] class ResourceController @Inject() (resourceManager: ResourceManager) extends Controller {

  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case GetAllResources => Success(resourceManager.getRegisteredResources)
  }

}
