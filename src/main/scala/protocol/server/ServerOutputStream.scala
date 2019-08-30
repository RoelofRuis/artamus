package protocol.server

import java.io.ObjectOutputStream

import protocol.Event
import protocol.MessageTypes.{DataResponse, EventResponse}

private[protocol] class ServerOutputStream(out: ObjectOutputStream) {

  def sendData(message: Any): Unit = {
    out.writeObject(DataResponse)
    out.writeObject(Right(message))
  }

  def sendError(message: String): Unit = {
    out.writeObject(DataResponse)
    out.writeObject(Left(message))
  }

  def sendEvent(message: Event): Unit = {
    out.writeObject(EventResponse)
    out.writeObject(message)
  }

}
