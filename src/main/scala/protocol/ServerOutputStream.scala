package protocol

import java.io.ObjectOutputStream

import protocol.MessageTypes.{EventMessage, ResponseMessage}

private[protocol] class ServerOutputStream(out: ObjectOutputStream) {

  def sendResponse(success: Boolean): Unit = {
    out.writeObject(ResponseMessage)
    out.writeObject(success)
  }

  def sendEvent(message: Event): Unit = {
    out.writeObject(EventMessage)
    out.writeObject(message)
  }

}
