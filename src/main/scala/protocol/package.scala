import scala.reflect.ClassTag

package object protocol {

  trait Control

  trait Command

  trait Query {
    type Res
  }

  trait ControlHandler {
    def handle[A <: Control](message: A): Boolean
  }

  trait CommandHandler {
    def handle[A <: Command : ClassTag](command: A): Boolean
  }

  trait QueryHandler {
    def handle[A <: Query : ClassTag](query: A): A#Res
  }

  final case class ServerBindings(
    commandHandler: CommandHandler,
    controlHandler: ControlHandler,
    queryHandler: QueryHandler
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
