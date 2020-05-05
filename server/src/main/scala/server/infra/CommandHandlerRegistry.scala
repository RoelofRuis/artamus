package server.infra

import java.util.concurrent.ConcurrentHashMap

import nl.roelofruis.artamus.core.api.Command
import javax.annotation.concurrent.ThreadSafe
import javax.inject.Singleton
import server.api.CommandHandlerRegistration
import server.api.CommandRequest.CommandHandler

import scala.reflect.{ClassTag, classTag}

@Singleton @ThreadSafe
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