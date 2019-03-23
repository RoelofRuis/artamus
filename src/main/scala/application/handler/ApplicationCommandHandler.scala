package application.handler

import application.api.Commands.{CloseApplication, Command}

import scala.util.{Success, Try}

private[application] class ApplicationCommandHandler() extends CommandHandler {
  override def handle[Res]: PartialFunction[Command[Res], Try[Res]] = {
    case CloseApplication => Success(())
  }
}
