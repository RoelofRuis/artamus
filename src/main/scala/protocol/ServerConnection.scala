package protocol

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

private[protocol] class ServerConnection private[protocol] (socket: Socket) {

  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ServerInputStream(objectIn)
  private val out = new ServerOutputStream(objectOut)

  def sendEvent[A <: Event](message: A): Unit = {
    out.sendEvent(message)
  }

  def handleNext(commandHandler: Command => Boolean, controlHandler: Control => Boolean): Unit = {
    val response = in.readNext(commandHandler, controlHandler)

    out.sendResponse(response)
  }

  def close(): Unit = {
    objectOut.close()
    objectIn.close()
  }

}
