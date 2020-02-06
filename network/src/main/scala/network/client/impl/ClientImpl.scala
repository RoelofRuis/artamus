package network.client.impl

import java.util.concurrent.atomic.AtomicReference

import javax.annotation.concurrent.NotThreadSafe
import network.Exceptions.{CommunicationException, NotConnected, ResponseException, TransportException}
import network.client.api._
import network.client.impl.ClientImpl.{Connected, TransportState, Unconnected}

@NotThreadSafe // TODO: ensure thread safety!
private[client] final class ClientImpl[R <: { type Res }, E](
  config: ClientConfig,
  api: ClientAPI[E],
  scheduler: EventScheduler[E]
) extends ClientInterface[R] {

  private val transportState: AtomicReference[TransportState] = new AtomicReference[TransportState](Unconnected(true))

  override def send[A <: R](data: A): Either[CommunicationException, A#Res] = {
    getTransport match {
      case Left(ex) => Left(ex)
      case Right(conn) => conn.send[A, A#Res](data) match {
        case r @ Right(_) => r
        case l @ Left(_: ResponseException) => l
        case l @ Left(ex: TransportException) =>
          transportState.set(Unconnected(true))
          api.connectionLost(ex)
          l
      }
    }
  }

  private def getTransport: Either[CommunicationException, ClientTransport] = {
    transportState.get match {
      case Connected(transport) => Right(transport)
      case Unconnected(false) => Left(NotConnected)
      case Unconnected(true) =>
        api.connectingStarted()
        ClientTransportFactory.create(config, scheduler) match {
          case r @ Right(t) =>
            transportState.compareAndSet(Unconnected(true), Connected(t))
            api.connectionEstablished()
            r

          case l @ Left(ex) =>
            transportState.compareAndSet(Unconnected(true), Unconnected(false))
            api.connectingFailed(ex)
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