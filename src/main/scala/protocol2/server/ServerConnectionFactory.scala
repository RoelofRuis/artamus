package protocol2.server

import java.net.ServerSocket

import resource.{ManagedResource, Resource}

import scala.util.{Failure, Success, Try}

class ServerConnectionFactory(resource: ManagedResource[ServerSocket]) extends Resource[ServerConnection] {

  override def acquire: Either[Throwable, ServerConnection] = {
    resource.acquire.flatMap(server => Try { new ServerConnection(server.accept()) } match {
      case Success(connection) => Right(connection)
      case Failure(exception) => Left(exception)
    })
  }

  override def release(a: ServerConnection): Option[Throwable] = a.close.headOption // TODO: ensure proper implementation
}
