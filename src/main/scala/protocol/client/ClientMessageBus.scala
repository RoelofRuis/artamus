package protocol.client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import com.typesafe.scalalogging.LazyLogging
import protocol.Event

import scala.util.Try

private[protocol] class ClientMessageBus(socket: Socket, bindings: ClientBindings) extends MessageBus with LazyLogging {

  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ClientInputStream(objectIn)
  private val out = new ClientOutputStream(objectOut)

  override def sendControl[A <: protocol.Control](message: A): Option[protocol.Control#Res] = {
    logger.info(s"Send CONTROL [$message]")
    out.sendControl(message)
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  override def sendCommand[A <: protocol.Command](message: A): Option[protocol.Command#Res] = {
    logger.info(s"Send COMMAND [$message]")
    out.sendCommand(message)
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  override def sendQuery[A <: protocol.Query](message: A): Option[A#Res] = {
    logger.info(s"Send QUERY [$message]")
    out.sendQuery(message)
    val (response, events) = in.expectResponseMessage[A#Res]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  def close(): Unit = {
    objectOut.close()
    objectIn.close()
  }

  // TODO: improve error handling on failure cases
  private def handleEvents(events: List[Try[Event]]): Unit = events.foreach(_.foreach(bindings.eventDispatcher.handle))
}
