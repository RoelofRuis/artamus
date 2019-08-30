package protocol

import protocol.ServerInterface.ServerBindings

import scala.reflect.ClassTag

trait ServerInterface {

  def acceptConnections(bindings: ServerBindings): Unit

  def publishEvent[A <: Event](event: A): Unit

  def closeActiveConnection(): Unit

  def stopServer(): Unit

}

object ServerInterface {

  trait ControlDispatcher {
    def handle[C <: Control](control: C): Boolean
  }

  trait CommandDispatcher {
    def handle[C <: Command : ClassTag](command: C): Boolean
  }

  trait QueryDispatcher {
    def handle[Q <: Query : ClassTag](query: Q): Option[Q#Res]
  }

  final case class ServerBindings(
    commandHandler: CommandDispatcher,
    controlHandler: ControlDispatcher,
    queryHandler: QueryDispatcher
  )

}