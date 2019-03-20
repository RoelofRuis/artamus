package application.ports

import application.command.Command

import scala.util.Try

trait MessageBus {

  def execute[Res](command: Command[Res]): Try[Res]

}
