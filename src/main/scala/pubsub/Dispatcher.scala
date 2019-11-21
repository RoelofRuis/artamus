package pubsub

import server.Request // TODO: make this abstract!

import scala.reflect.ClassTag
import scala.language.reflectiveCalls

trait Dispatcher[A <: { type Res }] {

  def handleRequest[B <: A : ClassTag](msg: Request[B]): Option[B#Res]

  @deprecated
  def handle[B <: A : ClassTag](msg: B): Option[B#Res]

  def subscribeRequest[B <: A : ClassTag](f: Request[B] => B#Res): Unit

  @deprecated
  def subscribe[B <: A : ClassTag](f: B => B#Res): Unit

  def getSubscriptions: List[String]

}
