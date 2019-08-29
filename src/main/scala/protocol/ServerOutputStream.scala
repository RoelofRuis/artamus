package protocol

import java.io.ObjectOutputStream

class ServerOutputStream(out: ObjectOutputStream) {

  def sendResponse(success: Boolean): Unit = {
    out.writeObject(ResponseMessage)
    out.writeObject(success)
  }

  def sendEvent(message: Event): Unit = {
    out.writeObject(EventMessage)
    out.writeObject(message)
  }

  def close(): Unit = out.close() // TODO: maybe move this out of here

}
