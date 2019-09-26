package protocol.client

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import com.typesafe.scalalogging.LazyLogging
import protocol.{CommandRequest, Event, QueryRequest}

import scala.util.Try

private[protocol] class DefaultClient(port: Int, bindings: ClientBindings) extends ClientInterface with LazyLogging {

  private val socket = new Socket(InetAddress.getByName("localhost"), port)

  private lazy val objectIn = new ObjectInputStream(socket.getInputStream)
  private val objectOut = new ObjectOutputStream(socket.getOutputStream)

  private val in = new ClientInputStream(objectIn)

  override def sendCommand[A <: protocol.Command](message: A): Option[protocol.Command#Res] = {
    logger.info(s"Send COMMAND [$message]")
    objectOut.writeObject(CommandRequest(message))
    val (response, events) = in.expectResponseMessage[Boolean]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  override def sendQuery[A <: protocol.Query](message: A): Option[A#Res] = {
    logger.info(s"Send QUERY [$message]")
    objectOut.writeObject(QueryRequest(message))
    val (response, events) = in.expectResponseMessage[A#Res]

    handleEvents(events)

    logger.info(s"Received [$response]")
    response.toOption
  }

  def close(): Unit = {
    objectOut.close()
    objectIn.close()
    logger.info(s"Client closed")
  }

  // TODO: improve error handling on failure cases
  private def handleEvents(events: List[Try[Event]]): Unit = events.foreach(_.foreach(bindings.eventDispatcher.handle))
}
