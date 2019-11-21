package pubsub

import server.Request

import scala.reflect.{ClassTag, classTag}
import scala.language.reflectiveCalls

/* @NotThreadSafe */
class SimpleDispatcher[A <: Object { type Res }] extends Dispatcher[A] {

  private var handlers = Map[String, Any]()
  private var requestHandlers = Map[String, Any]()

  def handleRequest[B <: A : ClassTag](req: Request[B]): Option[B#Res] = {
    val key = req.data.getClass.getCanonicalName
    requestHandlers.get(key).map(_.asInstanceOf[Request[B] => B#Res](req))
  }

  def handle[B <: A : ClassTag](msg: B): Option[B#Res] = {
    val key = msg.getClass.getCanonicalName
    handlers.get(key).map(_.asInstanceOf[B => B#Res](msg))
  }

  def subscribeRequest[B <: A : ClassTag](f: Request[B] => B#Res): Unit = {
    val key = classTag[B].runtimeClass.getCanonicalName
    requestHandlers = requestHandlers.updated(key, f)
  }

  def subscribe[B <: A : ClassTag](f: B => B#Res): Unit = {
    val key = classTag[B].runtimeClass.getCanonicalName
    handlers = handlers.updated(key, f)
  }

  def getSubscriptions: List[String] = handlers.keys.toList

}
