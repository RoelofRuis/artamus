package protocol

import pubsub.{Dispatcher, Subscriber}

package object server {

  trait ServerInterface {

    def accept(): Unit

    def shutdown(): Unit

  }

  final case class ServerBindings(
    commandDispatcher: Dispatcher[Command],
    controlDispatcher: Dispatcher[Control],
    queryDispatcher: Dispatcher[Query],
    eventSubscriber: Subscriber[String, Event, Unit]
  )

}
