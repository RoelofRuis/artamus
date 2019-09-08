package protocol2

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import protocol2.ObjectSocketConnection.{ObjectInputStreamFactory, ObjectOutputStreamFactory}
import protocol2.resource.{ResourceFactory, ResourceManager}

import scala.util.{Failure, Try}

final class ObjectSocketConnection private (socket: Socket) {

  val inputStream = new ResourceManager[ObjectInputStream](new ObjectInputStreamFactory(socket))
  val outputStream = new ResourceManager[ObjectOutputStream](new ObjectOutputStreamFactory(socket))

  def write[A](obj: A): Try[Unit] = outputStream.get.flatMap(stream => Try { stream.writeObject(obj) })

  def read[A]: Try[A] = inputStream.get.flatMap(stream => Try { stream.readObject().asInstanceOf[A] })

  def close(): Iterable[Throwable] = List(closeSocket, inputStream.close, outputStream.close).flatten

  private def closeSocket: Iterable[Throwable] = Try { socket.close() } match {
    case Failure(ex) => List(ex)
    case _ => List()
  }
}

object ObjectSocketConnection {

  def apply(socket: Socket): ObjectSocketConnection = new ObjectSocketConnection(socket)

  def apply(inetAddress: InetAddress, port: Int): Try[ObjectSocketConnection] = {
    Try { new Socket(inetAddress, port) }.map(new ObjectSocketConnection(_))
  }

  class ObjectInputStreamFactory(socket: Socket) extends ResourceFactory[ObjectInputStream] {
    override def create: Try[ObjectInputStream] = Try { new ObjectInputStream(socket.getInputStream) }
    override def close(s: ObjectInputStream): Iterable[Throwable] = Try { s.close() }.fold(List(_), _ => List())
  }

  class ObjectOutputStreamFactory(socket: Socket) extends ResourceFactory[ObjectOutputStream] {
    override def create: Try[ObjectOutputStream] = Try { new ObjectOutputStream(socket.getOutputStream) }
    override def close(s: ObjectOutputStream): Iterable[Throwable] = Try { s.close() }.fold(List(_), _ => List())
  }

}