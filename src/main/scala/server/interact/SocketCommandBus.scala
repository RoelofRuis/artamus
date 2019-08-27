package server.interact

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{ServerSocket, SocketException}

import server.Logger
import server.api.Actions.Action
import server.handler.Handler
import server.interact.SocketCommandBus.{CommandMap, MissingHandlerException}
import javax.inject.Inject

import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Try}

// TODO: create nice coating for easy interaction inside application as well as driver interaction.
private[server] class SocketCommandBus @Inject() private (logger: Logger) {

  lazy val server = new ServerSocket(9999)

  private var handlers: CommandMap[Handler] = new CommandMap[Handler]()

  def run(): Unit = {
    try {
      while (! Thread.interrupted()) {
        val socket = server.accept()
        val input = new ObjectInputStream(socket.getInputStream)

        val command = input.readObject().asInstanceOf[Action]

        logger.io("SOCKET COMMAND", "IN", s"$command")

        val response = execute(command)

        logger.io("SOCKET COMMAND", "OUT", s"$response")

        val output = new ObjectOutputStream(socket.getOutputStream)

        output.writeObject(response)

        socket.close()
      }
    } catch {
      case _: SocketException => logger.debug("Socked closed unexpectedly")
    }
  }

  def execute[C <: Action: ClassTag](command: C): Try[C#Res] = {
    handlers
      .get[C](command)
      .map(handler => handler.f(command))
      .getOrElse(Failure(MissingHandlerException(s"No handler for command [$command]")))

  }

  def subscribeHandler[Cmd <: Action: ClassTag](h: Handler[Cmd]): Unit = {
    handlers = handlers.add[Cmd](h)
  }

  def close(): Unit = {
    server.close()
  }
}

object SocketCommandBus {

  import scala.language.higherKinds

  class CommandMap[V[_ <: Action]](inner: Map[String, Any] = Map()) {
    def add[A <: Action: ClassTag](value: V[A]): CommandMap[V] = {
      val realKey: String = classTag[A].runtimeClass.getCanonicalName
      new CommandMap(inner + ((realKey, value)))
    }

    def get[A <: Action: ClassTag](command: A): Option[V[A]] = {
      val realKey: String = command.getClass.getCanonicalName
      inner.get(realKey).map(_.asInstanceOf[V[A]])
    }
  }

  case class MissingHandlerException(msg: String) extends RuntimeException {
    override def toString: String = msg
  }
}