import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import application.api.Commands._

object Client extends App {

  val socket = new Socket(InetAddress.getByName("localhost"), 9999)

  val out = new ObjectOutputStream(socket.getOutputStream)

//  out.writeObject(StartRecording)
  out.writeObject(GetTrack(TrackID(1)))

  lazy val in = new ObjectInputStream(socket.getInputStream)

  println("Received: " + in.readObject())

  socket.close()
}
