package protocol

import java.io.ObjectOutputStream

import protocol.MessageTypes.{DataResponse, ErrorResponse, EventResponse}

private[protocol] class ServerOutputStream(out: ObjectOutputStream) {

  def sendData(message: Any): Unit = {
    out.writeObject(DataResponse)
    out.writeObject(message)
  }

  def sendError(message: String): Unit = {
    out.writeObject(ErrorResponse)
    out.writeObject(message)
  }

  def sendEvent(message: Event): Unit = {
    out.writeObject(EventResponse)
    out.writeObject(message)
  }

}
