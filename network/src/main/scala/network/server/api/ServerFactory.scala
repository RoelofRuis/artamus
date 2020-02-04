package network.server.api

import java.net.ServerSocket

import javax.inject.{Inject, Singleton}
import network.Exceptions.{ConnectionException, TransportException}
import network.server.impl.Server

import scala.util.{Failure, Success, Try}

@Singleton
final class ServerFactory[R, E] @Inject() (config: ServerConfig, api: ServerAPI[R, E]) {

  def create(): Either[TransportException, ServerInterface] = {
    Try { new ServerSocket(config.port) } match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(serverSocket) =>
        val server = new Server[R, E](serverSocket, api)

        Right(server)
    }
  }

}
