package protocol

import protocol.ClientInterface.EventListener

import scala.reflect.ClassTag

trait ClientInterface {

  def sendControl[A <: Control](message: A): Option[Boolean]

  def sendCommand[A <: Command](message: A): Option[Boolean]

  def sendQuery[A <: Query](message: A): Option[A#Res]

  def subscribe[A <: Event: ClassTag](listener: EventListener[A]): Unit

  def closeConnection(): Unit

}

object ClientInterface {

  final case class EventListener[C <: Event](f: C => Unit)

}
