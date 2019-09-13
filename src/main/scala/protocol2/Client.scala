package protocol2

import java.net.{InetAddress, Socket}

import resource.Resource

class Client private (socketResource: Resource[Socket]) extends Connection {

  private var connection: Option[SocketConnection] = None

  override def send(message: Any): Either[Iterable[Throwable], Unit] = getConnection.send(message)

  override def receive: Either[Iterable[Throwable], Object] = getConnection.receive

  override def isClosed: Boolean = socketResource.isClosed

  override def close: List[Throwable] = connection.map(_.close).getOrElse(List())

  private def getConnection: SocketConnection = {
    connection match {
      case None =>
        val newConn = new SocketConnection(socketResource)
        connection = Some(newConn)
        newConn

      case Some(conn) => conn
    }
  }
}

object Client {

  def apply(inetAddress: InetAddress, port: Int): Client = {
    new Client(Resource.wrapUnsafe[Socket](new Socket(inetAddress, port), _.close()))
  }

}