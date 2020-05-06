package server.api

import artamus.core.api.Command
import server.api.CommandRequest.CommandHandler

import scala.reflect.ClassTag

trait CommandHandlerRegistration {

  def register[A <: Command : ClassTag](h: CommandHandler[A]): Unit

}
