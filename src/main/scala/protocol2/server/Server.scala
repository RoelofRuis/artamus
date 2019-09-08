package protocol2.server

import java.net.ServerSocket

import com.typesafe.scalalogging.LazyLogging
import protocol2.resource.ResourceManager

import scala.util.{Failure, Success}

class Server(port: Int, messageHandler: MessageHandler) extends ServerInterface with LazyLogging {

  val serverManager = new ResourceManager[ServerSocket](new ServerSocketFactory(port))
  val connectionManager = new ResourceManager[ServerConnection](new ServerConnectionFactory(serverManager))

  def accept(): Unit = {
    while (! connectionManager.isClosed) {
      logger.info("Accepting new connection")

      connectionManager.get match {
        case Success(conn) => receive(conn)
        case Failure(ex) => logger.warn(s"Failed to accept connection [$ex]")
      }

      connectionManager.discard
    }

    logger.info("Closing server")
    serverManager.close
  }

  def publish(msg: Any): Unit = {
    connectionManager.get match {
      case Success(conn) => send(conn, msg)
      case Failure(ex) => logger.warn(s"No connection to publish to [$ex]")
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

  def close(): Unit = connectionManager.close.foreach(ex => logger.warn(s"Error when closing server [$ex]"))

}

