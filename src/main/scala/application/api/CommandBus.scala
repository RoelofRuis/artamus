package application.api

import application.api.Commands.Command

import scala.util.Try

trait CommandBus {

  def execute[Res](command: Command[Res]): Try[Res]

}
