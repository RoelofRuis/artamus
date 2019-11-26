package pubsub

import scala.reflect.ClassTag
import scala.language.{higherKinds, reflectiveCalls}
import scala.util.Try

trait Dispatcher[R[_] <: RequestContainer[_], A <: { type Res }] {

  def handle[B <: A : ClassTag](req: R[B]): Try[B#Res]

  def subscribe[B <: A : ClassTag](f: R[B] => Try[B#Res]): Unit

  def getSubscriptions: List[String]

}
