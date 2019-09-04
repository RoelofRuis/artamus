package protocol

import protocol.ServerInterface.{EventBus, ServerBindings}

import scala.reflect.ClassTag

trait ServerInterface {

  def acceptConnections(bindings: ServerBindings): Unit

  def getEventBus: EventBus

  def closeActiveConnection(): Unit

  def stopServer(): Unit

}

object ServerInterface {

  trait Dispatcher[A <: { type Res }] {

    def handle[B <: A : ClassTag](msg: B): Option[B#Res]

    def subscribe[B <: A : ClassTag](f: B => B#Res): Unit

    def getSubscriptions: List[String]

  }

  trait EventBus {
    def publishEvent[A <: Event](event: A): Unit
  }

  final case class ServerBindings(
    commandDispatcher: Dispatcher[Command],
    controlDispatcher: Dispatcher[Control],
    queryDispatcher: Dispatcher[Query]
  )

}