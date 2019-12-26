package protocol.v2.client.impl

import javax.annotation.concurrent.{GuardedBy, NotThreadSafe}
import protocol.v2.{Command2, Query2}
import protocol.v2.Exceptions.{NotConnected, ResponseException}
import protocol.v2.client.api._
import protocol.v2.client.impl.Client2.{Connected, TransportState, Unconnected}


@NotThreadSafe // TODO: ensure thread safety!
final class Client2(
  config: ClientConfig,
  eventScheduler: EventScheduler
) extends ClientInterface2 {

  @GuardedBy("transport") private var transport: TransportState = Unconnected(true)

  private def getTransport: Either[ResponseException, Transport] = {
    transport match {
      case Connected(transport) => Right(transport)
      case Unconnected(false) => Left(NotConnected)
      case Unconnected(true) =>
        TransportFactory.create(config, eventScheduler) match {
          case r @ Right(t) => transport.synchronized { transport = Connected(t) }
            r
          case l @ Left(_) => transport.synchronized { transport = Unconnected(false) }
            l
        }
    }
  }

  override def sendCommand[A <: Command2](command: A): Option[ResponseException] = {
    getTransport.flatMap(_.send[Command2, Unit](command)) match {
      case Right(_) => None
      case Left(ex) => Some(ex)
    }
  }

  override def sendQuery[A <: Query2](query: A): Either[ResponseException, A#Res] = {
    getTransport.flatMap(_.send[Query2, A#Res])
  }

}

object Client2 {

  sealed trait TransportState
  final case class Unconnected(canRetry: Boolean) extends TransportState
  final case class Connected(transport: Transport) extends TransportState

}