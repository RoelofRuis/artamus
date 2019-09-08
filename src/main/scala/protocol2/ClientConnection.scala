package protocol2

import java.net.InetAddress

import protocol2.resource.ResourceManager

class ClientConnection(inetAddress: InetAddress, port: Int) {

  private val socket = new SimpleObjectSocket(
    new ResourceManager[ObjectSocketConnection](
      new ClientObjectSocketFactory(inetAddress,port),
      reopenable = true
    )
  )

  def send[A](message: A): Either[Iterable[Throwable], Unit] = socket.send(message)

  def receive[A]: Either[Iterable[Throwable], A] = socket.receive[A]

  def close: Iterable[Throwable] = socket.close

}
