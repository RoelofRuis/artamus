package protocol

package object server {

  trait ServerInterface {

    def acceptConnections(bindings: ServerBindings): Unit

    def closeActiveConnection(): Unit

    def stopServer(): Unit

  }

  trait Publisher[A] {

    def publish(a: A): Unit

  }

  trait Subscriber[A] {

    def subscribe(name: String, f: A => Unit): Unit

    def unsubscribe(name: String): Unit

  }

  final case class ServerBindings(
    commandDispatcher: Dispatcher[Command],
    controlDispatcher: Dispatcher[Control],
    queryDispatcher: Dispatcher[Query],
    eventSubscriber: Subscriber[Event]
  )

}
