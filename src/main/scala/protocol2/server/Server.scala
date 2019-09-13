package protocol2.server

import java.net.{ServerSocket, Socket}

import com.typesafe.scalalogging.LazyLogging
import resource.Resource

class Server(serverSocket: Resource[ServerSocket]) extends ServerInterface with LazyLogging {

  def accept(): Unit = {
    while ( ! serverSocket.isClosed) {
      serverSocket.acquire match {
        case Right(ss) => openNewConnection(ss)
        case Left(ex) => logger.error(s"Unable to start server [$ex]")
      }

      serverSocket.release
    }
  }

  private def openNewConnection(serverSocket: ServerSocket): Unit = {
    Resource.wrapUnsafe[Socket](serverSocket.accept(), _.close()).acquire match {
      case Right(connection) =>
        println(connection)

      case Left(ex) => logger.error(s"Unable to accept new connection [$ex]")
    }
  }

  def close(): Unit = {
    logger.info("Closing server")
    serverSocket.close match {
      case Some(ex) => logger.error(s"Error when closing server [$ex]")
      case _ => logger.info("Server closed")
    }
  }

}

object Server {

  def apply(port: Int): Server = new Server(Resource.wrapUnsafe[ServerSocket](new ServerSocket(port), _.close()))

}
