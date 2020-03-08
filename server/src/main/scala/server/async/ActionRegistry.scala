package server.async

import java.util.concurrent.ConcurrentHashMap

import domain.interact.{Command, Event}
import domain.workspace.User
import javax.annotation.concurrent.ThreadSafe
import server.async.ActionRegistry.ActionHandler
import storage.api.DbIO

import scala.reflect.{ClassTag, classTag}
import scala.util.Try

@ThreadSafe
class ActionRegistry extends ActionRegistration {

  private val request = new ConcurrentHashMap[String, Any]

  def lookupHandler[A <: Command : ClassTag](command: A): Option[ActionHandler[A]] = {
    val key = command.getClass.getCanonicalName
    Option(request.get(key)).map(_.asInstanceOf[ActionHandler[A]])
  }

  def register[A <: Command : ClassTag](h: ActionHandler[A]): Unit = {
    val key = classTag[A].runtimeClass.getCanonicalName
    request.put(key, h)
  }

}

object ActionRegistry {

  final case class Action[A](user: User, db: DbIO, attributes: A)

  type ActionHandler[A] = Action[A] => Try[List[Event]]

}