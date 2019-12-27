package protocol.client.impl

import javax.annotation.concurrent.{GuardedBy, NotThreadSafe}
import protocol.{Command, CommandRequest, Query, QueryRequest}
import protocol.Exceptions.{NotConnected, CommunicationException}
import protocol.client.api._
import protocol.client.impl.Client.{Connected, TransportState, Unconnected}


@NotThreadSafe // TODO: ensure thread safety!
final class Client(
  config: ClientConfig,
  eventScheduler: EventScheduler
) extends ClientInterface {

  @GuardedBy("transport") private var transport: TransportState = Unconnected(true)

  private def getTransport: Either[CommunicationException, ClientTransport] = {
    transport match {
      case Connected(transport) => Right(transport)
      case Unconnected(false) => Left(NotConnected)
      case Unconnected(true) =>
        ClientTransportFactory.create(config, eventScheduler) match {
          case r @ Right(t) => transport.synchronized { transport = Connected(t) }
            r
          case l @ Left(_) => transport.synchronized { transport = Unconnected(false) }
            l
        }
    }
  }

  override def sendCommand[A <: Command](command: A): Option[CommunicationException] = {
    getTransport.flatMap(_.send[CommandRequest, Unit](CommandRequest(command))) match {
      case Right(_) => None
      case Left(ex) => Some(ex)
    }
  }

  override def sendQuery[A <: Query](query: A): Either[CommunicationException, A#Res] = {
    getTransport.flatMap(_.send[QueryRequest, A#Res](QueryRequest(query)))
  }

}

object Client {

  sealed trait TransportState
  final case class Unconnected(canRetry: Boolean) extends TransportState
  final case class Connected(transport: ClientTransport) extends TransportState

}