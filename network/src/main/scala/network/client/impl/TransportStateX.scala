package network.client.impl

import java.util.concurrent.atomic.AtomicReference

import javax.annotation.concurrent.ThreadSafe
import network.DataResponseMessage
import network.client.api.ClientCallbacks
import network.client.impl.TransportStateX.{Connected, State, Unconnected}

@ThreadSafe
private[client] class TransportStateX(callbacks: ClientCallbacks) { // TODO: rename!

  private val transportState: AtomicReference[State] = new AtomicReference[State](Unconnected())

  def becomeUnconnected(cause: Throwable): Unit = {
    transportState.get() match {
      case Connected(transport) =>
        transport.interrupt()
        transportState.set(Unconnected())
        callbacks.connectionLost(cause)

      case Unconnected() =>
    }
  }

  def becomeConnected(transport: ClientTransportThread): Unit = {
    transportState.set(Connected(transport))
    callbacks.connectionEstablished()
  }

  def getTransport: Option[ClientTransportThread] = {
    transportState.get match {
      case Connected(transport) => Some(transport)
      case _ => None
    }
  }

  def notifyUnexpectedResponse(obj: DataResponseMessage): Unit = {
    callbacks.receivedUnexpectedResponse(obj)
  }

}

object TransportStateX {

  sealed trait State
  final case class Unconnected() extends State
  final case class Connected(transport: ClientTransportThread) extends State

}
