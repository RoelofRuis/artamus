package protocol2.server

import java.net.Socket

import protocol2.resource.ResourceManager
import protocol2.{ObjectSocketConnection, SimpleObjectSocket}

class ServerConnection(s: Socket) {

  private val socket = new SimpleObjectSocket(
    new ResourceManager[ObjectSocketConnection](
      new ServerObjectSocketFactory(s)
    )
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
