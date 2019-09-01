package protocol.client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol.ClientInterface.EventListener
import protocol._

import scala.reflect.ClassTag
import scala.util.Try

private[protocol] class DefaultClient private[protocol] (port: Int) extends ClientInterface {

  private val socket = new Socket(InetAddress.getByName("localhost"), port)

  private val eventRegistry = new ClientEventRegistry()

  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ClientInputStream(objectIn)
  private val out = new ClientOutputStream(objectOut)

  def sendControl[A <: Control](message: A): Option[Boolean] = {
    out.sendControl(message)
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    response.toOption
  }

  def sendCommand[A <: Command](message: A): Option[Boolean] = {
    out.sendCommand(message)
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    response.toOption
  }

  def sendQuery[A <: Query](message: A): Option[A#Res] = {
    out.sendQuery(message)
    val (response, events) = in.expectResponseMessage[A#Res]

    handleEvents(events)

    response.toOption
  }

  // TODO: improve error handling on Failure case
  private def handleEvents(events: List[Try[Event]]): Unit = events.foreach(_.foreach(eventRegistry.publish))

  def subscribe[A <: Event: ClassTag](callback: EventListener[A]): Unit = eventRegistry.subscribe(callback)

  def closeConnection(): Unit = {
    objectOut.close()
    objectIn.close()
    socket.close()
  }

}
