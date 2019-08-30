package protocol.server

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import protocol.Event
import protocol.ServerInterface.ServerBindings

private[protocol] class ServerConnection (socket: Socket) {

  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ServerInputStream(objectIn)
  private val out = new ServerOutputStream(objectOut)

  def sendEvent[A <: Event](message: A): Unit = out.sendEvent(message)

  def handleNext(bindings: ServerBindings): Unit = {
    in.readNext(bindings) match {
      case Right(response) => out.sendData(response)
      case Left(error) => out.sendError(error)
    }
  }

  def close(): Unit = {
    objectOut.close()
    objectIn.close()
  }

}
