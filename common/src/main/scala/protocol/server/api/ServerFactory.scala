package protocol.server.api

import java.net.ServerSocket

import javax.inject.{Inject, Singleton}
import protocol.Exceptions.{ConnectionException, TransportException}
import protocol.server.impl.Server

import scala.util.{Failure, Success, Try}

@Singleton
final class ServerFactory[E] @Inject() (config: ServerConfig, api: ServerAPI[E]) {

  def create(): Either[TransportException, ServerInterface] = {
    Try { new ServerSocket(config.port) } match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(serverSocket) =>
        val server = new Server[E](serverSocket, api)

        Right(server)
    }
  }

}
