package protocol

package object client {

  trait ClientInterface {

    def open(bindings: ClientBindings): MessageBus

    def close(): Unit

  }

  trait MessageBus {
    def sendControl[A <: Control](message: A): Option[Control#Res]

    def sendCommand[A <: Command](message: A): Option[Command#Res]

    def sendQuery[A <: Query](message: A): Option[A#Res]
  }

  final case class ClientBindings(
    eventDispatcher: Dispatcher[Event]
  )

}
