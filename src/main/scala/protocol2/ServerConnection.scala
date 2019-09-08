package protocol2

import java.net.Socket

import protocol2.resource.ResourceManager

class ServerConnection(s: Socket) {

  private val socket = new SimpleObjectSocket(
    new ResourceManager[ObjectSocketConnection](
      new ServerObjectSocketFactory(s),
      reopenable = false
    )
  )

  def send[A](message: A): Either[Iterable[Throwable], Unit] = socket.send(message)

  def receive[A]: Either[Iterable[Throwable], A] = socket.receive[A]

  def close: Iterable[Throwable] = socket.close

}
