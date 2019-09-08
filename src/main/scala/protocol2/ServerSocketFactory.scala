package protocol2

import java.net.ServerSocket

import protocol2.resource.ResourceFactory

import scala.util.{Failure, Success, Try}

class ServerSocketFactory(port: Int) extends ResourceFactory[ServerSocket] {

  override def create: Try[ServerSocket] = Try { new ServerSocket(port) }

  override def close(a: ServerSocket): Iterable[Throwable] = Try { a.close() } match {
    case Success(_) => List()
    case Failure(ex) => List(ex)
  }

}
