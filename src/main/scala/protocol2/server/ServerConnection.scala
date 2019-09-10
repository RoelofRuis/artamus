package protocol2.server

import java.net.Socket

import protocol2.SimpleObjectSocket
import resource.ManagedResource.managed

class ServerConnection(s: Socket) {

  private val socket = new SimpleObjectSocket(
    managed(new ServerObjectSocketResource(s))
  )

  def send(message: Any): Either[Iterable[Throwable], Unit] =
    socket.send(message) match {
      case Left(ex) => Left(socket.close ++ ex)
      case r: Right[_, _] => r
    }

  def receive: Either[Iterable[Throwable], Object] =
    socket.receive match {
      case Left(ex) => Left(socket.close ++ ex)
      case r: Right[_, _] => r
    }

  def isClosed: Boolean = socket.isClosed

  def close: Iterable[Throwable] = socket.close

}
