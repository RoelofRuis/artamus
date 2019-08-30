package client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol.ClientEventRegistry.Callback
import protocol._
import server.api.Server.Disconnect
import server.api.Track.{AddQuarterNote, SetKey, SetTimeSignature, TrackSymbolsUpdated}

import scala.reflect.ClassTag
import scala.util.Try

object ClientApp extends App {

  val c = new Client(9999)

  c.subscribeToEvent(Callback[TrackSymbolsUpdated.type](_ => println("Callback A")))

  c.sendCommand(SetTimeSignature(4, 4))
  c.sendCommand(SetKey(0))
  c.sendCommand(AddQuarterNote(64))

  c.subscribeToEvent(Callback[TrackSymbolsUpdated.type](_ => println("Callback B")))

  c.sendCommand(AddQuarterNote(66))

  c.sendControlMessage(Disconnect(false))

  c.close()

}

class Client(port: Int) {

  private val socket = new Socket(InetAddress.getByName("localhost"), port)
  private val eventRegistry = new ClientEventRegistry()
  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private lazy val in = new ClientInputStream(objectIn, eventRegistry)
  private val out = new ClientOutputStream(objectOut)

  def sendControlMessage[A <: Control](message: A): Try[Boolean] = {
    out.sendControl(message)
    in.expectResponseMessage
  }

  def sendCommand[A <: Command](command: A): Try[Boolean] = {
    out.sendCommand(command)
    in.expectResponseMessage
  }

  def subscribeToEvent[A <: Event: ClassTag](callback: Callback[A]): Unit = eventRegistry.subscribe(callback)


  def close(): Unit = {
    objectOut.close()
    objectIn.close()
    socket.close()
  }

}
