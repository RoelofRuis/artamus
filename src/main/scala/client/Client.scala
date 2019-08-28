package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import server.api.Application.StopServer
import server.api.Track.{Print, SetKey, SetTimeSignature}
import server.api.commands.Command

import scala.util.Try

object Client extends App {

  val c = new CommandClient(9999)

  c.send(SetTimeSignature(4, 4))
  c.send(SetKey(0))
  c.send(Print)
  c.send(StopServer)

}

class CommandClient(port: Int) {

  def send[A <: Command](command: A): Try[A#Res] = {
    val socket = new Socket(InetAddress.getByName("localhost"), port)
    val out = new ObjectOutputStream(socket.getOutputStream)
    lazy val in = new ObjectInputStream(socket.getInputStream)
    out.writeObject(command)
    val response = in.readObject().asInstanceOf[Try[A#Res]]
    out.close()
    in.close()
    socket.close()
    response
  }

}