package protocol2

import java.net.ServerSocket

import protocol2.resource.ResourceFactory

import scala.util.Try

class ServerConnectionFactory(serverSocket: ServerSocket) extends ResourceFactory[ServerConnection] {

  override def create: Try[ServerConnection] = Try { new ServerConnection(serverSocket.accept()) }

  override def close(a: ServerConnection): Iterable[Throwable] = a.close

}
