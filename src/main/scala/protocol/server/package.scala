package protocol

package object server {

  trait ServerInterface {

    def acceptConnections(bindings: ServerBindings): Unit

    def getEventBus: EventBus

    def closeActiveConnection(): Unit

    def stopServer(): Unit

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
