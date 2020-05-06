package network.client.impl

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import network.Exceptions.{ConnectionException, TransportException}
import network.client.api.ClientConfig

import scala.util.{Failure, Success, Try}

private[client] object ClientTransportFactory {

  def create(
    config: ClientConfig,
    scheduler: EventScheduler,
    transportState: TransportState
  ): Either[TransportException, ClientTransportThread] = {
    val transport = for {
      socket <- Try { new Socket(InetAddress.getByName(config.host), config.port) }
      objOut <- Try { new ObjectOutputStream(socket.getOutputStream) }
      objIn <- Try { new ObjectInputStream(socket.getInputStream) }
    } yield new ClientTransportThread(socket, objIn, objOut, scheduler, transportState)

    transport match {
      case Failure(ex) => Left(ConnectionException(ex))
      case Success(clientTransport) =>
        clientTransport.setDaemon(true)
        clientTransport.start()
        Right(clientTransport)
    }
  }

}
