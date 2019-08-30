package protocol

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol.ClientEventRegistry.Callback

import scala.reflect.ClassTag
import scala.util.Try

private[protocol] class ClientConnection private[protocol] (port: Int) {

  private val socket = new Socket(InetAddress.getByName("localhost"), port)
  private val eventRegistry = new ClientEventRegistry()
  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ClientInputStream(objectIn, eventRegistry)
  private val out = new ClientOutputStream(objectOut)

  def sendControlMessage[A <: Control](message: A): Try[Boolean] = {
    out.sendControl(message)
    in.expectResponseMessage
  }

  def sendCommand[A <: Command](message: A): Try[Boolean] = {
    out.sendCommand(message)
    in.expectResponseMessage
  }

  def subscribeToEvent[A <: Event: ClassTag](callback: Callback[A]): Unit = eventRegistry.subscribe(callback)

  def close(): Unit = {
    objectOut.close()
    objectIn.close()
    socket.close()
  }

}
