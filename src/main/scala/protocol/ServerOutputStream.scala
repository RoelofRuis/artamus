package protocol

import java.io.ObjectOutputStream

class ServerOutputStream(out: ObjectOutputStream) {

  def sendResponse(message: Any): Unit = { // TODO: can we make this more typesafe?
    out.writeObject(ResponseMessage)
    out.writeObject(message)
  }

  def sendEvent(message: Event): Unit = {
    out.writeObject(EventMessage)
    out.writeObject(message)
  }

  def close(): Unit = out.close() // TODO: maybe move this out of here

}
