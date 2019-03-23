package application.handler

import application.api.Commands.Command

import scala.util.Try

private[application] trait CommandHandler {

  def handle[Res]: PartialFunction[Command[Res], Try[Res]]

}
