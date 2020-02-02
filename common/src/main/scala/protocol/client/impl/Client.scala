package protocol.client.impl

import javax.annotation.concurrent.{GuardedBy, NotThreadSafe}
import protocol.Exceptions.{CommunicationException, NotConnected, ResponseException, TransportException}
import protocol.RequestMessage
import protocol.client.api._
import protocol.client.impl.Client.{Connected, TransportState, Unconnected}

@NotThreadSafe // TODO: ensure thread safety!
private[client] final class Client[C, Q <: { type Res }, E](
  config: ClientConfig,
  eventScheduler: EventScheduler[Either[ConnectionEvent, E]],
  @GuardedBy("transport") private var transport: TransportState = Unconnected(true)
) extends ClientInterface[C, Q] {

  override def sendCommand(command: C): Option[CommunicationException] = {
    sendWithTransport[RequestMessage[C], Unit](RequestMessage(command)).left.toOption
  }

  override def sendQuery[A <: Q](query: A): Either[CommunicationException, A#Res] = {
    sendWithTransport[RequestMessage[A], A#Res](RequestMessage(query))
  }

  private def getTransport: Either[CommunicationException, ClientTransport] = {
    transport match {
      case Connected(transport) => Right(transport)
      case Unconnected(false) => Left(NotConnected)
      case Unconnected(true) =>
        eventScheduler.schedule(Left(ConnectingStarted))
        ClientTransportFactory.create(config, eventScheduler) match {
          case r @ Right(t) =>
            transport.synchronized { transport = Connected(t) }
            eventScheduler.schedule(Left(ConnectionMade))
            r

          case l @ Left(_) =>
            transport.synchronized { transport = Unconnected(false) }
            eventScheduler.schedule(Left(ConnectingFailed))
            l
        }
    }
  }

  private def sendWithTransport[A, B](data: A): Either[CommunicationException, B] = {
    getTransport match {
      case Left(ex) => Left(ex)
      case Right(conn) => conn.send[A, B](data) match {
        case r @ Right(_) => r
        case l @ Left(_: ResponseException) => l
        case l @ Left(_: TransportException) =>
          transport.synchronized { transport = Unconnected(true) }
          eventScheduler.schedule(Left(ConnectionLost))
          l
      }
    }
  }

}

object Client {

  sealed trait TransportState
  final case class Unconnected(canRetry: Boolean) extends TransportState
  final case class Connected(transport: ClientTransport) extends TransportState

}