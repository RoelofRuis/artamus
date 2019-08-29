package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol._
import server.api.Server.Disconnect
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature}

import scala.util.Try

object Client extends App {

  val c = new StreamComposeClient(9999)

//  c.registerListener[TrackChanged.type] // TODO: expand on this

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(AddQuarterNote(64))
  c.sendControlMessage(Disconnect(true))

  c.close()

}

class StreamComposeClient(port: Int) {

  private val (socket: Socket, in: ClientInputStream, out: ClientOutputStream) = connect()

  private def connect(): (Socket, ClientInputStream, ClientOutputStream) = {
    val socket = new Socket(InetAddress.getByName("localhost"), port)

    val out = new ClientOutputStream(new ObjectOutputStream(socket.getOutputStream))
    lazy val in = new ClientInputStream(new ObjectInputStream(socket.getInputStream))
    (socket, in, out)
  }

  // TODO: Make sure this goes correctly: try now means either sending success or calculation success..!
  def sendControlMessage[A <: Control](message: A): Try[Boolean] = {
    out.sendControl(message)
    in.expectControlResponse
  }

  def sendCommand[A <: Command](command: A): Try[A#Res] = {
    out.sendCommand(command)
    in.expectCommandResponse[A]
  }

  def registerListener[A <: Event](listener: => A): Unit = ???

  def close(): Unit = {
    out.close()
    in.close()
    socket.close()
  }

}
