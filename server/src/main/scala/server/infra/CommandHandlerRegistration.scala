package server.infra

import domain.interact.Command
import server.infra.CommandRequest.CommandHandler

import scala.reflect.ClassTag

trait CommandHandlerRegistration {

  def register[A <: Command : ClassTag](h: CommandHandler[A]): Unit

}
