package protocol.server

import protocol.ServerInterface.Dispatcher

import scala.languageFeature.reflectiveCalls
import scala.reflect.{ClassTag, classTag}

private[protocol] class SimpleDispatcher[A <: Object { type Res }] extends Dispatcher[A] {

  private var handlers = Map[String, Any]()

  def handle[B <: A : ClassTag](msg: B): Option[B#Res] = {
    val key = msg.getClass.getCanonicalName
    handlers.get(key).map(_.asInstanceOf[B => B#Res](msg))
  }

  def subscribe[B <: A : ClassTag](f: B => B#Res): Unit = {
    val key = classTag[B].runtimeClass.getCanonicalName
    handlers = handlers.updated(key, f)
  }

  def getSubscriptions: List[String] = handlers.keys.toList

}
