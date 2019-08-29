package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import server.api.Track.{Print, SetKey, SetTimeSignature}
import server.api.messages._
import util.SafeObjectInputStream

import scala.util.Try

object Client extends App {

  val c = new StreamComposeClient(9999)

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(Print)
  c.sendControlMessage(Disconnect(true))

  c.close()

}

class StreamComposeClient(port: Int) {

  private val (socket: Socket, in: SafeObjectInputStream, out: ObjectOutputStream) = connect()

  private def connect(): (Socket, SafeObjectInputStream, ObjectOutputStream) = {
    val socket = new Socket(InetAddress.getByName("localhost"), port)
    val out = new ObjectOutputStream(socket.getOutputStream)
    lazy val in = new SafeObjectInputStream(new ObjectInputStream(socket.getInputStream))
    (socket, in, out)
  }

  def sendControlMessage[A <: Control](message: A): Boolean = {
    out.writeObject(ControlMessage)
    out.writeObject(message)

    in.readObject[ServerResponseMessage]()
      .flatMap {
        case ResponseMessage => in.readObject[Boolean]()
      }
      .fold(_ => false, identity)
  }

  def sendCommand[A <: Command](command: A): Try[A#Res] = {
    out.writeObject(CommandMessage)
    out.writeObject(command)

    val response = in.readObject[ServerResponseMessage]()
      .flatMap {
        case ResponseMessage => in.readObject[Try[A#Res]]()
      }

    println(response)

    response.flatMap(identity)
  }

  def close(): Unit = {
    out.close()
    in.close()
    socket.close()
  }

  def registerListener[A <: Event](listener: => A): Unit = ???

}
