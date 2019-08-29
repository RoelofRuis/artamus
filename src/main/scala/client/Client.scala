package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature}
import server.api.messages._
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import util.SafeObjectInputStream

import scala.util.{Failure, Try}

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
        case EventMessage =>
          println(s"Event: ${in.readObject[Event]()}")
          Failure(new NotImplementedException())
      }
      .fold(_ => false, identity)
  }

  def sendCommand[A <: Command](command: A): Try[A#Res] = {
    out.writeObject(CommandMessage)
    out.writeObject(command)

    val response = in.readObject[ServerResponseMessage]()
      .flatMap {
        case ResponseMessage => in.readObject[Try[A#Res]]()
        case EventMessage =>
          println(s"Event: ${in.readObject[Event]()}")
          Failure(new NotImplementedException())
      }

    println(response)

    response.flatMap(identity)
  }

  def registerListener[A <: Event](listener: => A): Unit = ???

  def close(): Unit = {
    out.close()
    in.close()
    socket.close()
  }

}
