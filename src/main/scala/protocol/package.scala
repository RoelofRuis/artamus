import scala.reflect.ClassTag
import scala.util.Try

package object protocol {

  private[protocol] sealed trait ServerRequestMessage
  private[protocol] case object ControlMessage extends ServerRequestMessage
  private[protocol] case object CommandMessage extends ServerRequestMessage

  private[protocol] sealed trait ServerResponseMessage
  private[protocol] case object ResponseMessage extends ServerResponseMessage
  private[protocol] case object EventMessage extends ServerResponseMessage

  // Public API (move to separate file)
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
