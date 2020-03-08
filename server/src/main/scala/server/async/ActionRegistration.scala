package server.async

import domain.interact.Command
import server.async.ActionRegistry.ActionHandler

import scala.reflect.ClassTag

trait ActionRegistration {

  def register[A <: Command : ClassTag](h: ActionHandler[A]): Unit

}
