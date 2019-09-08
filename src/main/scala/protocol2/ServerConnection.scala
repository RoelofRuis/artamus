package protocol2

import java.net.Socket

import protocol2.resource.ResourceManager

class ServerConnection(s: Socket) {

  private val socket = new SimpleObjectSocket(
    new ResourceManager[ObjectSocketConnection](
      new ServerObjectSocketFactory(s)
    )
  )

  def send[A](message: A): Either[Iterable[Throwable], Unit] =
    socket.send(message) match {
      case Left(ex) => Left(socket.close ++ ex)
      case r: Right[_, _] => r
    }

  def receive[A]: Either[Iterable[Throwable], A] =
    socket.receive[A] match {
      case Left(ex) => Left(socket.close ++ ex)
      case r: Right[_, _] => r
    }

  def isClosed: Boolean = socket.isClosed

  def close: Iterable[Throwable] = socket.close

}
