package application.interact

import java.util.concurrent.SynchronousQueue

import application.api.CommandBus
import application.api.Commands.Command
import application.handler.Handler
import application.interact.SynchronousCommandBus.{CommandMap, MissingHandlerException, Task}
import javax.inject.Inject

import scala.reflect.runtime.universe.{TypeTag, typeTag}
import scala.util.{Failure, Try}

private[application] class SynchronousCommandBus @Inject() private (logger: Logger) extends CommandBus {

  private val queueIn: SynchronousQueue[Task[_]] = new SynchronousQueue[Task[_]]()
  private val queueOut: SynchronousQueue[Try[_]] = new SynchronousQueue[Try[_]]()

  private var handlers: CommandMap[Command, Handler] = new CommandMap[Command, Handler]()

  def execute[C <: Command: TypeTag](command: C): Try[C#Res] = synchronized {
    logger.io("MESSAGE BUS", "EXE", s"$command")

    val res = handlers.get[C]
      .map { handler =>
        queueIn.put(Task[C](handler, command))
        queueOut.take().flatMap(res => Try(res.asInstanceOf[C#Res]))
      }
      .getOrElse(Failure(MissingHandlerException(s"No handler for command [${typeTag[command.type]}")))

    logger.io("MESSAGE BUS", "RES",s"$res")

    res
  }

  def subscribeHandler[Cmd <: Command: TypeTag](h: Handler[Cmd]): Unit = {
    handlers = handlers.add[Cmd](h)
  }

  def handleNext(): Command = {
    val task = queueIn.take()

    queueOut.add(task.run)

    task.command.asInstanceOf[Command]
  }

}

object SynchronousCommandBus {

  private case class Task[C <: Command](handler: Handler[C], command: C) {
    def run: Try[C#Res] = handler.f(command)
  }

  import scala.language.higherKinds

  class CommandMap[K, V[_ <: Command]](
    inner: Map[TypeTag[_], Any] = Map()
  ) {
    def add[A <: Command: TypeTag](value: V[A]): CommandMap[K,V] = {
      val realKey: TypeTag[_] = typeTag[A]
      new CommandMap(inner + ((realKey, value)))
    }

    def get[A <: Command: TypeTag]: Option[V[A]] = {
      val realKey: TypeTag[_] = typeTag[A]
      inner.get(realKey).map(_.asInstanceOf[V[A]])
    }
  }

  case class MissingHandlerException(msg: String) extends RuntimeException {
    override def toString: String = msg
  }
}
