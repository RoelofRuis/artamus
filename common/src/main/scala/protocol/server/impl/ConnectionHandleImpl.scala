package protocol.server.impl

import java.io.ObjectOutputStream
import java.util.UUID

import protocol.EventResponseMessage
import protocol.Exceptions.WriteException
import protocol.server.api.ConnectionHandle

import scala.util.{Failure, Success, Try}

private[server] final case class ConnectionHandleImpl[A] (
  private val eventOut: ObjectOutputStream,
  id: UUID = UUID.randomUUID()
) extends ConnectionHandle[A] {

  override def sendEvent(event: A): Option[WriteException] = {
    Try { eventOut.writeObject(EventResponseMessage[A](event)) } match {
      case Success(_) => None
      case Failure(ex) => Some(WriteException(ex))
    }
  }

}
