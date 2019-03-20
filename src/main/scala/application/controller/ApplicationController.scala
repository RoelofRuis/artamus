package application.controller

import application.command.ApplicationCommand.CloseApplication
import application.command.Command

import scala.util.{Success, Try}

private[application] class ApplicationController() extends Controller {
  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case CloseApplication => Success(())
  }
}
