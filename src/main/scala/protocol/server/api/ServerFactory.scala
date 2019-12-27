package protocol.server.api

import java.net.ServerSocket

import javax.inject.{Inject, Singleton}
import protocol.Exceptions.{ConnectionException, TransportException}
import protocol.server.impl.Server

import scala.util.{Failure, Success, Try}

@Singleton
final class ServerFactory @Inject() (config: ServerConfig, api: ServerAPI) {

  def create(): Either[TransportException, ServerInterface] = {
    Try { new ServerSocket(config.port) } match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(serverSocket) =>
        val server = new Server(serverSocket, api)

        Right(server)
    }
  }

}
