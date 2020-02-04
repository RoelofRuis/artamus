package network.client.impl

import javax.annotation.concurrent.{GuardedBy, NotThreadSafe}
import network.Exceptions.{CommunicationException, NotConnected, ResponseException, TransportException}
import network.client.api._
import network.client.impl.Client.{Connected, TransportState, Unconnected}

@NotThreadSafe // TODO: ensure thread safety!
private[client] final class Client[R <: { type Res }, E](
  config: ClientConfig,
  eventScheduler: EventScheduler[Either[ConnectionEvent, E]],
  @GuardedBy("transport") private var transport: TransportState = Unconnected(true)
) extends ClientInterface[R] {

  override def send[A <: R](request: A): Either[CommunicationException, A#Res] = sendWithTransport[A, A#Res](request)

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