package protocol

import java.io.ObjectOutputStream

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
