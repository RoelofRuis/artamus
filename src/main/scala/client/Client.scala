package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import server.api.Application.StopServer
import server.api.Track.{Print, SetKey, SetTimeSignature}
import server.api.messages._
import util.SafeObjectInputStream

import scala.util.{Failure, Success, Try}

object Client extends App {

  val c = new CommandClient(9999)

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(Print)
  c.sendCommand(StopServer)

}

class CommandClient(port: Int) {

  def sendCommand[A <: Command](command: A): Try[A#Res] = {
    val socket = new Socket(InetAddress.getByName("localhost"), port)
    val out = new ObjectOutputStream(socket.getOutputStream)
    lazy val in = new SafeObjectInputStream(new ObjectInputStream(socket.getInputStream))
    out.writeObject(CommandMessage)
    out.writeObject(command)

    val response = in.readObject[ServerResponseMessage]()
      .collect {
        case ResponseMessage => in.readObject[Try[A#Res]]()
      }

    out.close()
    in.close()
    socket.close()
    response.flatMap {
      case Success(x) => x
      case Failure(x) => Failure(x)
    }
  }

}