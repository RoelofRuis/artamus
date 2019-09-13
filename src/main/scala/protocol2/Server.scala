package protocol2

import java.net.{ServerSocket, Socket}

import com.typesafe.scalalogging.LazyLogging
import resource.Resource

class Server(serverSocketResource: Resource[ServerSocket]) extends ServerInterface with LazyLogging {

  def accept(): Unit = {
    while ( ! serverSocketResource.isClosed) {
      serverSocketResource.acquire match {
        case Right(serverSocket) =>
          receive(newConnection(serverSocket))

        case Left(ex) =>
          println(ex.printStackTrace())
          logger.error(s"Unable to start server [$ex]")
      }
    }
  }

  private def receive(conn: SocketConnection) {
    while ( ! conn.isClosed) {
      conn.receive match {
        case Right(obj) =>
          logger.info(s"Received [$obj]")
          conn.send(obj)

        case Left(ex) => logger.error(s"unable to receive [$ex]")
      }
    }
  }

  private def newConnection(serverSocket: ServerSocket): SocketConnection = {
    new SocketConnection(Resource.wrapUnsafe[Socket](serverSocket.accept(), _.close()))
  }

  def close(): Unit = {
    logger.info("Closing server")
    serverSocketResource.close match {
      case Some(ex) => logger.error(s"Error when closing server [$ex]")
      case _ => logger.info("Server closed")
    }
  }

}

object Server {

  def apply(port: Int): Server = new Server(Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close()))

}
