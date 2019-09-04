package protocol.client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import com.typesafe.scalalogging.LazyLogging
import protocol._

import scala.reflect.ClassTag
import scala.util.Try

private[protocol] class DefaultClient private[protocol] (port: Int) extends ClientInterface with LazyLogging {

  private val socket = new Socket(InetAddress.getByName("localhost"), port)

  private val eventDispatcher = protocol.createDispatcher[Event]() // TODO: maybe pull out and pass via constructor?

  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ClientInputStream(objectIn)
  private val out = new ClientOutputStream(objectOut)

  def sendControl[A <: Control](message: A): Option[Boolean] = {
    logger.info(s"Send CONTROL [$message]")
    out.sendControl(message)
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  def sendCommand[A <: Command](message: A): Option[Boolean] = {
    logger.info(s"Send COMMAND [$message]")
    out.sendCommand(message)
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  def sendQuery[A <: Query](message: A): Option[A#Res] = {
    logger.info(s"Send QUERY [$message]")
    out.sendQuery(message)
    val (response, events) = in.expectResponseMessage[A#Res]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  // TODO: improve error handling on Failure case
  private def handleEvents(events: List[Try[Event]]): Unit = events.foreach(_.foreach(eventDispatcher.handle))

  def subscribe[A <: Event: ClassTag](callback: A => A#Res): Unit = eventDispatcher.subscribe[A](callback)

  def closeConnection(): Unit = {
    objectOut.close()
    objectIn.close()
    socket.close()
  }

}
