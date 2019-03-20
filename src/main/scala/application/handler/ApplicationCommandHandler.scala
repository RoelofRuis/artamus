package application.handler

import application.command.ApplicationCommand.CloseApplication
import application.command.Command

import scala.util.{Success, Try}

private[application] class ApplicationCommandHandler() extends CommandHandler {
  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case CloseApplication => Success(())
  }
}
