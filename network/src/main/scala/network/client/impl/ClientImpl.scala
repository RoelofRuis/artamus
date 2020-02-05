package network.client.impl

import javax.annotation.concurrent.{GuardedBy, NotThreadSafe}
import network.Exceptions.{CommunicationException, NotConnected, ResponseException, TransportException}
import network.client.api._
import network.client.impl.ClientImpl.{Connected, TransportState, Unconnected}

@NotThreadSafe // TODO: ensure thread safety!
private[client] final class ClientImpl[R <: { type Res }, E](
  config: ClientConfig,
  api: ClientAPI[E],
  scheduler: EventScheduler[E],
  @GuardedBy("transport") private var transport: TransportState = Unconnected(true)
) extends ClientInterface[R] {

  override def send[A <: R](request: A): Either[CommunicationException, A#Res] = sendWithTransport[A, A#Res](request)

  private def getTransport: Either[CommunicationException, ClientTransport] = {
    transport match {
      case Connected(transport) => Right(transport)
      case Unconnected(false) => Left(NotConnected)
      case Unconnected(true) =>
        api.connectingStarted()
        ClientTransportFactory.create(config, scheduler) match {
          case r @ Right(t) =>
            transport.synchronized { transport = Connected(t) }
            api.connectionEstablished()
            r

          case l @ Left(ex) =>
            transport.synchronized { transport = Unconnected(false) }
            api.connectingFailed(ex)
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
        case l @ Left(ex: TransportException) =>
          transport.synchronized { transport = Unconnected(true) }
          api.connectionLost(ex)
          l
      }
    }
  }

}

object ClientImpl {

  sealed trait TransportState
  final case class Unconnected(canRetry: Boolean) extends TransportState
  final case class Connected(transport: ClientTransport) extends TransportState

}