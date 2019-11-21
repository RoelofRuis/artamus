package pubsub

import scala.language.reflectiveCalls
import scala.reflect.{ClassTag, classTag}

/* @NotThreadSafe */
class SimpleDispatcher[R[_] <: RequestContainer[_], A <: Object { type Res }] extends Dispatcher[R, A] {

  private var request = Map[String, Any]()

  def handle[B <: A : ClassTag](req: R[B]): Option[B#Res] = {
    val key = req.attributes.getClass.getCanonicalName
    request.get(key).map(_.asInstanceOf[R[B] => B#Res](req))
  }

  def subscribe[B <: A : ClassTag](f: R[B] => B#Res): Unit = {
    val key = classTag[B].runtimeClass.getCanonicalName
    request = request.updated(key, f)
  }

  def getSubscriptions: List[String] = request.keys.toList

}
