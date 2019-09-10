package protocol2

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.{InetAddress, Socket}

import resource.ManagedResource
import resource.ManagedResource.managed
import resource.Resource.safe

import scala.util.{Failure, Try}

final class ObjectSocketConnection private (socket: Socket) {

  val inputStream: ManagedResource[ObjectInputStream] = managed(safe[ObjectInputStream](new ObjectInputStream(socket.getInputStream), _.close()))
  val outputStream: ManagedResource[ObjectOutputStream] = managed(safe[ObjectOutputStream](new ObjectOutputStream(socket.getOutputStream), _.close()))

  def write(obj: Any): Try[Unit] = outputStream.acquire match {
    case Right(stream) => Try { stream.writeObject(obj) }
    case Left(ex) => Failure(ex)
  }

  def read: Try[Object] = inputStream.acquire match {
    case Right(stream) => Try { stream.readObject() }
    case Left(ex) => Failure(ex)
  }

  def close(): Iterable[Throwable] = List(closeSocket, inputStream.close, outputStream.close).flatten

  private def closeSocket: Option[Throwable] = Try { socket.close() } match {
    case Failure(ex) => Some(ex)
    case _ => None
  }
}

object ObjectSocketConnection {

  def apply(socket: Socket): ObjectSocketConnection = new ObjectSocketConnection(socket)

  def apply(inetAddress: InetAddress, port: Int): Try[ObjectSocketConnection] = {
    Try { new Socket(inetAddress, port) }.map(new ObjectSocketConnection(_))
  }

}