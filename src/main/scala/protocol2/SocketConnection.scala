package protocol2

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.net.Socket

import resource.Resource

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

final class SocketConnection (socket: Resource[Socket]) extends Connection {

  private val inputStream: Resource[ObjectInputStream] = socket.transformUnsafe(
    s => new ObjectInputStream(s.getInputStream),
    _.close()
  )

  private val outputStream: Resource[ObjectOutputStream] = socket.transformUnsafe(
    s => new ObjectOutputStream(s.getOutputStream),
    _.close()
  )

  def send(message: Any): Either[Iterable[Throwable], Unit] = {
    outputStream.acquire match {
      case Right(stream) => Try { stream.writeObject(message) } match {
        case Success(_) => Right(())
        case Failure(ex) => Left(close :+ ex)
      }
      case Left(ex) => Left(close :+ ex)
    }
  }

  def receive: Either[Iterable[Throwable], Object] = {
    inputStream.acquire match {
      case Right(stream) => Try { stream.readObject() } match {
        case Success(r) => Right(r)
        case Failure(ex) => Left(close :+ ex)
      }
      case Left(ex) => Left(close :+ ex)
    }
  }

  def isClosed: Boolean = socket.isClosed

  def close: List[Throwable] = List(socket.close, inputStream.close, outputStream.close).flatten

}
