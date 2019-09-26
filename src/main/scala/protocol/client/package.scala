package protocol

import pubsub.Dispatcher

package object client {

  trait ClientInterface {
    def sendCommand[A <: Command](message: A): Option[Command#Res]

    def sendQuery[A <: Query](message: A): Option[A#Res]

    def close(): Unit
  }

  final case class ClientBindings(
    eventDispatcher: Dispatcher[Event]
  )

}
