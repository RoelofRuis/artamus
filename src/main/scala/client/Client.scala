package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature}
import server.api.messages._
import util.SafeObjectInputStream

import scala.util.{Success, Try}

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

    // TODO: probably we can wrap both streams with a specific API for Server and Client basic interaction

    val out = new ObjectOutputStream(socket.getOutputStream)
    lazy val in = new SafeObjectInputStream(new ObjectInputStream(socket.getInputStream))
    (socket, in, out)
  }

  def sendControlMessage[A <: Control](message: A): Boolean = {
    out.writeObject(ControlMessage)
    out.writeObject(message)

    // TODO: combine and clean up these loops
    def read: Boolean = {
      in.readObject[ServerResponseMessage]()
        .flatMap {
          case ResponseMessage => in.readObject[Boolean]()
          case EventMessage =>
            println(s"Event: ${in.readObject[Event]()}")
            Success(read)
        }
        .fold(_ => false, identity)
    }

    read
  }

  def sendCommand[A <: Command](command: A): Try[A#Res] = {
    out.writeObject(CommandMessage)
    out.writeObject(command)

    // TODO: combine and clean up these loops
    def read: Try[Try[A#Res]] = {
      in.readObject[ServerResponseMessage]()
        .flatMap {
          case ResponseMessage => in.readObject[Try[A#Res]]()
          case EventMessage =>
            println(s"Event: ${in.readObject[Event]()}")
            read
        }
    }

    val response = read

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
