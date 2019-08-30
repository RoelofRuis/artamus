package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol._
import server.api.Server.Disconnect
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature, TrackSymbolsUpdated}

import scala.util.Try

object Client extends App {

  val c = new StreamComposeClient(9999)

  c.subscribeToEventStream {
    case TrackSymbolsUpdated => println("Track symbols updated!")
  }

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(AddQuarterNote(64))
  c.sendControlMessage(Disconnect(true))

  c.close()

}

class StreamComposeClient(port: Int) {

  private val (socket: Socket, in: ClientInputStream, out: ClientOutputStream, eventRegistry: ClientEventRegistry) = connect()

  // TODO: clean up creation logic
  private def connect(): (Socket, ClientInputStream, ClientOutputStream, ClientEventRegistry) = {
    val socket = new Socket(InetAddress.getByName("localhost"), port)
    val eventRegistry = new ClientEventRegistry()

    val out = new ClientOutputStream(new ObjectOutputStream(socket.getOutputStream))
    lazy val in = new ClientInputStream(new ObjectInputStream(socket.getInputStream), eventRegistry)
    (socket, in, out, eventRegistry)
  }

  def sendControlMessage[A <: Control](message: A): Try[Boolean] = {
    out.sendControl(message)
    in.expectResponseMessage
  }

  def sendCommand[A <: Command](command: A): Try[Boolean] = {
    out.sendCommand(command)
    in.expectResponseMessage
  }

  def subscribeToEventStream(listener: Event => Unit): Unit = eventRegistry.subscribe(listener)

  def close(): Unit = {
    out.close()
    in.close()
    socket.close()
  }

}
