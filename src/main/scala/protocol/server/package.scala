package protocol

import pubsub.Subscriber

package object server {

  trait ServerInterface {

    def acceptConnections(bindings: ServerBindings): Unit

    def closeActiveConnection(): Unit

    def stopServer(): Unit

  }

  final case class ServerBindings(
    commandDispatcher: Dispatcher[Command],
    controlDispatcher: Dispatcher[Control],
    queryDispatcher: Dispatcher[Query],
    eventSubscriber: Subscriber[Event]
  )

}
