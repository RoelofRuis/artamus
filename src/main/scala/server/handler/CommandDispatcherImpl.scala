package server.handler

import protocol.{Command, CommandDispatcher}
import server.handler.CommandDispatcherImpl.CommandMap

import scala.reflect.{ClassTag, classTag}

private[server] class CommandDispatcherImpl extends CommandDispatcher {

  private var handlers: CommandMap[CommandHandler] = new CommandMap[CommandHandler]()

  override def handle[C <: Command : ClassTag](command: C): Boolean = {
    handlers
      .get[C](command)
      .exists(handler => handler.f(command))
  }

  def subscribe[Cmd <: Command: ClassTag](h: CommandHandler[Cmd]): Unit = {
    handlers = handlers.add[Cmd](h)
  }

}

object CommandDispatcherImpl {

  import scala.language.higherKinds

  class CommandMap[V[_ <: Command]](inner: Map[String, Any] = Map()) {
    def add[A <: Command: ClassTag](value: V[A]): CommandMap[V] = {
      val realKey: String = classTag[A].runtimeClass.getCanonicalName
      new CommandMap(inner + ((realKey, value)))
    }

    def get[A <: Command: ClassTag](command: A): Option[V[A]] = {
      val realKey: String = command.getClass.getCanonicalName
      inner.get(realKey).map(_.asInstanceOf[V[A]])
    }
  }
}