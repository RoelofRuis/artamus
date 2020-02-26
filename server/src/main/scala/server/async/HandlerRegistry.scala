package server.async

import java.util.concurrent.ConcurrentHashMap

import scala.reflect.{ClassTag, classTag}
import domain.interact.{Command, Event}
import domain.workspace.User
import javax.annotation.concurrent.ThreadSafe
import server.async.HandlerRegistry.TaskHandler
import storage.api.DbIO

import scala.util.Try

@ThreadSafe
class HandlerRegistry {

  private val request = new ConcurrentHashMap[String, Any]

  def lookupHandler[A <: Command : ClassTag](command: A): Option[TaskHandler[A]] = {
    val key = command.getClass.getCanonicalName
    Option(request.get(key)).map(_.asInstanceOf[TaskHandler[A]])
  }

  def registerHandler[A <: Command : ClassTag](h: TaskHandler[A]): Unit = {
    val key = classTag[A].runtimeClass.getCanonicalName
    request.put(key, h)
  }

}

object HandlerRegistry {

  type TaskHandler[A] = (User, DbIO, A) => Try[List[Event]]

}