package pubsub

import scala.reflect.ClassTag
import scala.language.{higherKinds, reflectiveCalls}

trait Dispatcher[R[_] <: RequestContainer[_], A <: { type Res }] {

  def handle[B <: A : ClassTag](req: R[B]): Option[B#Res]

  def subscribe[B <: A : ClassTag](f: R[B] => B#Res): Unit

  def getSubscriptions: List[String]

}
