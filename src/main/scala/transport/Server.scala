package transport

import java.net.{ServerSocket, Socket}
import java.util.concurrent.ArrayBlockingQueue

import com.typesafe.scalalogging.LazyLogging
import resource.Resource

import scala.util.{Failure, Success, Try}

/** @deprecated Moved to protocol */
class Server(serverSocketResource: Resource[ServerSocket]) extends ServerInterface with LazyLogging {

  private val connectionQueue = new ArrayBlockingQueue[SocketConnection](1)

  def accept(): Unit = {
    while ( ! serverSocketResource.isClosed) {
      serverSocketResource.acquire match {
        case Right(serverSocket) => Try { serverSocket.accept() } match {
          case Success(sock) =>
            connectionQueue.put(new SocketConnection(Resource.wrapUnsafe[Socket](sock, _.close())))

          case Failure(ex) => logger.error(s"Unable to accept connections [$ex]")
        }

        case Left(ex) => logger.error(s"Unable to start server [$ex]")
      }
    }
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
