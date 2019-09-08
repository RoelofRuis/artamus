package protocol2

import java.net.ServerSocket

import protocol2.resource.{ResourceFactory, ResourceManager}

import scala.util.Try

class ServerConnectionFactory(manager: ResourceManager[ServerSocket]) extends ResourceFactory[ServerConnection] {

  override def create: Try[ServerConnection] = manager.get.flatMap(server => Try { new ServerConnection(server.accept()) })

  override def close(a: ServerConnection): Iterable[Throwable] = a.close

}
