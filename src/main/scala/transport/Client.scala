package transport

import java.net.{InetAddress, Socket}

import resource.Resource

class Client private (socketResource: Resource[Socket]) extends Connection {

  private var connection: Option[SocketConnection] = None

  override def send(message: Any): Either[Seq[Throwable], Unit] = getConnection.send(message)

  override def receive: Either[Seq[Throwable], Object] = getConnection.receive

  override def isClosed: Boolean = socketResource.isClosed

  override def close: Seq[Throwable] = connection.map(_.close).getOrElse(List())

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