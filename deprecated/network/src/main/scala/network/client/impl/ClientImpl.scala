package network.client.impl

import javax.annotation.concurrent.NotThreadSafe
import network.Exceptions.{CommunicationException, ResponseException, TransportException}
import network.client.api._

@NotThreadSafe // TODO: ensure thread safety!
private[client] final class ClientImpl[R <: { type Res }](
  config: ClientConfig,
  transportState: TransportState,
  scheduler: EventScheduler
) extends ClientInterface[R] {

  override def send[A <: R](data: A): Either[CommunicationException, A#Res] = {
    transportState.getTransport match {
      case None =>
        ClientTransportFactory.create(config, scheduler, transportState) match {
          case Right(t) =>
            transportState.becomeConnected(t)
            transmit(t, data)

          case Left(ex) => Left(ex)
        }
      case Some(transport) => transmit(transport, data)
    }
  }

  private def transmit[A <: R](transport: ClientTransportThread, data: A): Either[CommunicationException, A#Res] = {
    transport.send[A, A#Res](data) match {
      case r @ Right(_) => r
      case l @ Left(_: ResponseException) => l
      case l @ Left(ex: TransportException) =>
        transportState.becomeUnconnected(ex)
        l
    }
  }

}
