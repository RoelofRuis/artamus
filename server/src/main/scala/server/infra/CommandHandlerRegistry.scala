package server.infra

import java.util.concurrent.ConcurrentHashMap

import domain.interact.Command
import javax.annotation.concurrent.ThreadSafe
import server.infra.CommandRequest.CommandHandler

import scala.reflect.{ClassTag, classTag}

@ThreadSafe
class CommandHandlerRegistry extends CommandHandlerRegistration {

  private val request = new ConcurrentHashMap[String, Any]

  def register[A <: Command : ClassTag](h: CommandHandler[A]): Unit = {
    val key = classTag[A].runtimeClass.getCanonicalName
    request.put(key, h)
  }

  def lookupHandler[A <: Command : ClassTag](command: A): Option[CommandHandler[A]] = {
    val key = command.getClass.getCanonicalName
    Option(request.get(key)).map(_.asInstanceOf[CommandHandler[A]])
  }

}