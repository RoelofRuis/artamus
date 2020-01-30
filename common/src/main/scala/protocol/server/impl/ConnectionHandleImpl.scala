package protocol.server.impl

import java.io.ObjectOutputStream
import java.util.UUID

import protocol.{Event, EventResponse}
import protocol.Exceptions.WriteException
import protocol.server.api.ConnectionHandle

import scala.util.{Failure, Success, Try}

private[server] final case class ConnectionHandleImpl (
  private val eventOut: ObjectOutputStream,
  id: UUID = UUID.randomUUID()
) extends ConnectionHandle {

  override def sendEvent(event: Event): Option[WriteException] = {
    Try { eventOut.writeObject(EventResponse(event)) } match {
      case Success(_) => None
      case Failure(ex) => Some(WriteException(ex))
    }
  }

}
