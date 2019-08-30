import scala.reflect.ClassTag

// TODO: split into classes required for Server and classes required for Client
package object protocol {

  trait Control

  trait Command

  trait Query {
    type Res
  }

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

  trait Event

  final case class EventListener[C <: Event](f: C => Unit)

  trait Server {

    def acceptConnections(bindings: ServerBindings): Unit

    def publishEvent[A <: Event](event: A): Unit

    def closeActiveConnection(): Unit

    def stopServer(): Unit

  }

  trait Client {

    def sendControl[A <: Control](message: A): Option[Boolean]

    def sendCommand[A <: Command](message: A): Option[Boolean]

    def sendQuery[A <: Query](message: A): Option[A#Res]

    def subscribe[A <: Event: ClassTag](listener: EventListener[A]): Unit

    def closeConnection(): Unit

  }

  def client(port: Int): Client = new DefaultClient(port)
  def server(port: Int): Server = new SingleConnectionServer(port)

}
