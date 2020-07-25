package network.server.impl

import java.util.UUID

import network.EventResponseMessage
import network.Exceptions.WriteException
import network.server.api.ConnectionHandle

import scala.util.{Failure, Success, Try}

private[server] final case class ConnectionHandleImpl[A] (
  private val eventOut: SynchronizedOutputStream,
  id: UUID = UUID.randomUUID()
) extends ConnectionHandle[A] {

  override def sendEvent(event: A): Option[WriteException] = {
    Try { eventOut.writeObject(EventResponseMessage[A](event)) } match {
      case Success(_) => None
      case Failure(ex) => Some(WriteException(ex))
    }
  }

}
