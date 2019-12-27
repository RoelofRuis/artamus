package protocol.server.impl

import java.io.{EOFException, IOException, ObjectInputStream, ObjectOutputStream}
import java.net.Socket
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import protocol.Exceptions.WriteException
import protocol.server.api.{ConnectionRef, ServerAPI}
import protocol.server.impl.ServerConnectionFactory.ServerConnection
import protocol.{Event, EventResponse}

import scala.util.{Failure, Success, Try}

private[server] class ServerConnectionFactory(server: ServerAPI) extends LazyLogging {

  def connect(socket: Socket): Try[Runnable] = {
    try {

      lazy val objectIn = new ObjectInputStream(socket.getInputStream)
      val objectOut = new ObjectOutputStream(socket.getOutputStream)
      val connection = ServerConnection(objectOut)

      Success(new Runnable {
        override def run(): Unit = {
          server.connectionOpened(connection)

          try {
            while (socket.isConnected) {
              val request = objectIn.readObject()

              val mainResponse = server.handleRequest(connection, request)
              val afterRequestResponse = server.afterRequest(connection, mainResponse)

              objectOut.writeObject(afterRequestResponse)
            }
          } catch {
            case _: EOFException => logger.info("EOF: Client hang up")
            case ex: IOException => logger.error("Connection thread encountered IOException", ex)
          } finally {
            server.connectionClosed(connection)
            socket.close()
          }
        }
      })
    } catch {
      case ex: Exception => Failure(ex)
    }
  }
}

object ServerConnectionFactory {

  private[ServerConnectionFactory] final case class ServerConnection (
    private val eventOut: ObjectOutputStream,
    id: UUID = UUID.randomUUID()
  ) extends ConnectionRef {
    override def sendEvent(event: Event): Option[WriteException] = {
      Try { eventOut.writeObject(EventResponse(event)) } match {
        case Success(_) => None
        case Failure(ex) => Some(WriteException(ex))
      }
    }
  }

}
