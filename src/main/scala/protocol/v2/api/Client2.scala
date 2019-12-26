package protocol.v2.api

import javax.annotation.concurrent.{GuardedBy, NotThreadSafe}
import protocol.v2.api.Client2.{Connected, TransportState, Unconnected}


@NotThreadSafe // TODO: ensure thread safety!
final class Client2() extends ClientInterface2 { // TODO: move to impl

  private val stateGuard = new Object()
  @GuardedBy("stateGuard") private var transport: TransportState = Unconnected(true)

  private def getTransport: Either[ResponseException, Transport] = {
    transport match {
      case Connected(transport) => Right(transport)
      case Unconnected(false) => Left(NotConnected)
      case Unconnected(true) =>
        // create transport
        // - if no error: update state and return transport

        ???
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