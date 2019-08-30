import scala.reflect.ClassTag
import scala.util.Try

package object protocol {

  trait Event

  case class EventListener[C <: Event](f: C => Unit)

  trait Control

  trait Command

  trait Query {
    type Res
  }

  trait Server {

    def acceptConnections(commandHandler: Command => Boolean, controlHandler: Control => Boolean): Unit

    def publishEvent[A <: Event](event: A): Unit

    def closeActiveConnection(): Unit

    def stopServer(): Unit

  }

  trait Client {

    def sendControl[A <: Control](message: A): Try[Boolean]

    def sendCommand[A <: Command](message: A): Try[Boolean]

    def subscribe[A <: Event: ClassTag](listener: EventListener[A]): Unit

    def closeConnection(): Unit

  }

  def client(port: Int): Client = new DefaultClient(port)
  def server(port: Int): Server = new SingleConnectionServer(port)

}
