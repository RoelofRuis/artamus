package protocol2

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import scala.util.{Failure, Success, Try}

private[protocol] final case class ObjectSocketConnection private (socket: Socket, in: ObjectInputStream, out: ObjectOutputStream) {

  def write[A](obj: A): Try[Unit] = Try { out.writeObject(obj) }

  def read[A]: Try[A] = Try { in.readObject().asInstanceOf[A] }

  def close(): Option[Throwable] = {
    Try {
      socket.close()
      in.close()
      out.close()
    } match {
      case Failure(ex) => Some(ex)
      case Success(_) => None
    }
  }
}

object ObjectSocketConnection {

  def apply(socket: Socket): Try[ObjectSocketConnection] = for {
    input <- Try { new ObjectInputStream(socket.getInputStream) }
    output <- Try { new ObjectOutputStream(socket.getOutputStream) }
  } yield ObjectSocketConnection(socket, input, output)

  def apply(inetAddress: InetAddress, port: Int): Try[ObjectSocketConnection] = for {
    socket <- Try { new Socket(inetAddress, port) }
    input <- Try { new ObjectInputStream(socket.getInputStream) }
    output <- Try { new ObjectOutputStream(socket.getOutputStream) }
  } yield ObjectSocketConnection(socket, input, output)

}