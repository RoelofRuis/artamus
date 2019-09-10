package protocol2

import java.net.InetAddress

import protocol2.resource.Resource

import scala.util.{Failure, Success}

class ObjectSocketConnectionFactory (inetAddress: InetAddress, port: Int) extends Resource[ObjectSocketConnection] {

  override def acquire: Either[Throwable, ObjectSocketConnection] =
    ObjectSocketConnection(inetAddress, port) match {
      case Success(value) => Right(value)
      case Failure(ex) => Left(ex)
    }

  override def release(a: ObjectSocketConnection): Option[Throwable] = a.close().headOption // TODO: ensure proper implementation
}
