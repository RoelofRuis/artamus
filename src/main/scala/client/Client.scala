package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import server.api.commands.Application.StopServer
import server.api.commands.Track.{Print, SetKey, SetTimeSignature}
import server.util.Rational

object Client extends App {

  val socket = new Socket(InetAddress.getByName("localhost"), 9999)

  val out = new ObjectOutputStream(socket.getOutputStream)

//  out.writeObject(Print)
//  out.writeObject(SetTimeSignature(Rational(4, 4)))
//  out.writeObject(SetKey(0))
  out.writeObject(StopServer)

  lazy val in = new ObjectInputStream(socket.getInputStream)

  println("Received: " + in.readObject())

  socket.close()
}
