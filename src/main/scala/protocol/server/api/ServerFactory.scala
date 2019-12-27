package protocol.server.api

import java.net.ServerSocket

import javax.inject.Singleton
import protocol.Exceptions.{ConnectionException, TransportException}
import protocol.server.impl.Server
import server.ServerConfig

import scala.util.{Failure, Success, Try}

@Singleton
final class ServerFactory(config: ServerConfig, api: ServerAPI) {

  def create(): Either[TransportException, ServerInterface] = {
    Try { new ServerSocket(config.port) } match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(serverSocket) =>
        val server = new Server(serverSocket, api)

        Right(server)
    }
  }

}
