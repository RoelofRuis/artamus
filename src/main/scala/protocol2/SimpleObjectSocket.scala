package protocol2

import java.net.InetAddress

import resource.ManagedResource

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

class SimpleObjectSocket(connection: ManagedResource[ObjectSocketConnection]) extends ObjectSocket {

  def send(message: Any): Either[Iterable[Throwable], Unit] = {
    connection.acquire match {
      case Right(conn) => conn.write(message) match {
        case Success(_) => Right(())
        case Failure(ex) => Left(connection.release.toList :+ ex)
      }
      case Left(ex) => Left(List(ex))
    }
  }

  def receive: Either[Iterable[Throwable], Object] = {
    connection.acquire match {
      case Right(conn) => conn.read match {
        case Success(r) => Right(r)
        case Failure(ex) => Left(connection.release.toList :+ ex)
      }
      case Left(ex) => Left(List(ex))
    }
  }

  def isClosed: Boolean = connection.isClosed

  def close: Iterable[Throwable] = connection.close

}

object SimpleObjectSocket {

  def apply(inetAddress: InetAddress, port: Int): SimpleObjectSocket =
    new SimpleObjectSocket(
      ManagedResource.wrapTry[ObjectSocketConnection](ObjectSocketConnection(inetAddress, port), res => Try(res.close())))
}