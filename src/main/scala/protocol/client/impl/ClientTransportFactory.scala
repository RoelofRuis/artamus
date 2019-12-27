package protocol.client.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol.Exceptions.{ConnectionException, TransportException}
import protocol.client.api.ClientConfig

import scala.util.{Failure, Success, Try}

object ClientTransportFactory {

  def create(config: ClientConfig, eventScheduler: EventScheduler): Either[TransportException, ClientTransport] = {
    val transport = for {
      socket <- Try { new Socket(InetAddress.getByName(config.host), config.port) }
      objOut <- Try { new ObjectOutputStream(socket.getOutputStream) }
      objIn <- Try { new ObjectInputStream(socket.getInputStream) }
    } yield new ClientTransportThread(socket, objIn, objOut, eventScheduler)

    transport match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(clientTransport) =>
        clientTransport.setDaemon(true)
        clientTransport.start()
        Right(clientTransport)
    }
  }

}
