package protocol2.server

import java.net.ServerSocket

import com.typesafe.scalalogging.LazyLogging
import resource.ManagedResource
import resource.ManagedResource.managed
import resource.Resource.safe

class Server(port: Int, messageHandler: MessageHandler) extends ServerInterface with LazyLogging {

  val serverSocket: ManagedResource[ServerSocket] = managed(safe[ServerSocket](new ServerSocket(port), _.close()))
  val serverConnection: ManagedResource[ServerConnection] = managed[ServerConnection](new ServerConnectionFactory(serverSocket))

  def accept(): Unit = {
    while (! serverConnection.isClosed) {
      logger.info("Accepting new connection")

      serverConnection.acquire match {
        case Right(conn) => receive(conn)
        case Left(ex) => logger.warn(s"Failed to accept connection [$ex]")
      }

      serverConnection.release
    }

    logger.info("Closing server")
    serverSocket.close
  }

  def publish(msg: Any): Unit = {
    serverConnection.acquire match {
      case Right(conn) => send(conn, msg)
      case Left(ex) => logger.warn(s"No connection to publish to [$ex]")
    }
  }

  private def receive(conn: ServerConnection): Unit = {
    while ( ! conn.isClosed) {
      conn.receive match {
        case Right(a) =>
          logger.info(s"Server received [$a]")
          messageHandler.handle(a) match {
            case Right(obj) => send(conn, obj)
            case Left(ex) => logger.warn(s"Error when handling message [$ex]")
          }
        case Left(ex) => logger.warn(s"Error when receiving a message [$ex]")
      }
    }
  }

  private def send(conn: ServerConnection, msg: Any): Unit = {
    conn.send(msg) match {
      case Right(_) => logger.info(s"Server sent [$msg]")
      case Left(ex) => logger.warn(s"Error when sending a message [$ex]")
    }
  }

  def close(): Unit = serverConnection.close.foreach(ex => logger.warn(s"Error when closing server [$ex]"))

}

