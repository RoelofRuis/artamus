package pubsub

import scala.reflect.ClassTag
import scala.language.reflectiveCalls

trait Dispatcher[A <: { type Res }] {

  def handle[B <: A : ClassTag](msg: B): Option[B#Res]

  def subscribe[B <: A : ClassTag](f: B => B#Res): Unit

  def getSubscriptions: List[String]

}
