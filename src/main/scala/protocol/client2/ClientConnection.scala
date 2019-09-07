package protocol.client2

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import scala.util.{Failure, Success, Try}

private[protocol] final case class ClientConnection(socket: Socket, in: ObjectInputStream, out: ObjectOutputStream) {
  def write[A](message: A): Try[Unit] = Try { out.writeObject(message) }
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
