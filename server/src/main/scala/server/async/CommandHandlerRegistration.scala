package server.async

import domain.interact.Command
import server.async.CommandRequest.CommandHandler

import scala.reflect.ClassTag

trait CommandHandlerRegistration {

  def register[A <: Command : ClassTag](h: CommandHandler[A]): Unit

}
