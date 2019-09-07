package protocol.client2

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import scala.util.{Failure, Success, Try}

class SimpleConnectionFactory (
  inetAddress: InetAddress,
  port: Int,
) extends ClientConnectionFactory {

  def connect(): Either[String, ClientConnection] = {
    val connection = for {
      socket <- Try { new Socket(inetAddress, port) }
      input <- Try { new ObjectInputStream(socket.getInputStream) }
      output <- Try { new ObjectOutputStream(socket.getOutputStream) }
    } yield ClientConnection(socket, input, output)

    connection match {
      case Success(con) => Right(con)
      case Failure(err) => Left(s"Unable to open connection [$err]")
    }
  }

}

