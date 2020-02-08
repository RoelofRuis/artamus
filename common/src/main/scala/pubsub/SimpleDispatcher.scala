package pubsub

import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Try}

/* @NotThreadSafe */
class SimpleDispatcher[R[_] <: Dispatchable[_], A <: Object { type Res }] extends Dispatcher[R, A] {

  private var request = Map[String, Any]()

  def handle[B <: A : ClassTag](req: R[B]): Try[B#Res] = {
    val key = req.attributes.getClass.getCanonicalName
    request
      .get(key)
      .map(_.asInstanceOf[R[B] => Try[B#Res]](req))
      .getOrElse(Failure(new Exception("Missing handler")))
  }

  def subscribe[B <: A : ClassTag](f: R[B] => Try[B#Res]): Unit = {
    val key = classTag[B].runtimeClass.getCanonicalName
    request = request.updated(key, f)
  }

  def getSubscriptions: List[String] = request.keys.toList

}
