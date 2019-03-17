package application.controller

import application.command.Command

import scala.util.Try

private[application] trait Controller {

  def handle[Res]: PartialFunction[Command[Res], Try[Res]]

}
