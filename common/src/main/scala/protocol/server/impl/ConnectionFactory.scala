package protocol.server.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.ServerSocket

import protocol.Exceptions.{ConnectionException, TransportException}
import protocol.server.api.ServerAPI

import scala.util.{Failure, Success, Try}

private[server] object ConnectionFactory {

  def acceptNext[R, E](serverSocket: ServerSocket, api: ServerAPI[R, E]): Either[TransportException, Connection[R, E]] = {
    val transport = for {
      socket <- Try { serverSocket.accept() }
      objOut <- Try { new ObjectOutputStream(socket.getOutputStream) }
      objIn <- Try { new ObjectInputStream(socket.getInputStream) }
    } yield new Connection(api, socket, objIn, objOut)

    transport match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(serverTransport) => Right(serverTransport)
    }
  }

}
