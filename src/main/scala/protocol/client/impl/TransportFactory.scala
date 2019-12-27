package protocol.client.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol.Exceptions.{ConnectException, TransportException}
import protocol.client.api.ClientConfig

import scala.util.{Failure, Success, Try}

object TransportFactory {

  def create(config: ClientConfig, eventScheduler: EventScheduler): Either[TransportException, Transport] = {
    val result = for {
      socket <- Try { new Socket(InetAddress.getByName(config.host), config.port) }
      objOut <- Try { new ObjectOutputStream(socket.getOutputStream) }
      objIn <- Try { new ObjectInputStream(socket.getInputStream) }
    } yield new TransportThread(socket, objIn, objOut, eventScheduler)

    result match {
      case Failure(ex) => Left(ConnectException(ex))
      case Success(clientTransport) =>
        clientTransport.setDaemon(true)
        clientTransport.start()
        Right(clientTransport)
    }
  }

}
