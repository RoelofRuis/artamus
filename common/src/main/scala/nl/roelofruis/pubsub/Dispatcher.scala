package nl.roelofruis.pubsub

import scala.reflect.ClassTag
import scala.util.Try

trait Dispatcher[R[_] <: Dispatchable[_], A <: { type Res }] {

  def handle[B <: A : ClassTag](req: R[B]): Try[B#Res]

  def subscribe[B <: A : ClassTag](f: R[B] => Try[B#Res]): Unit

  def getSubscriptions: List[String]

}
