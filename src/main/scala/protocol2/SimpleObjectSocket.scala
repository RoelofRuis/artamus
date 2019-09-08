package protocol2

import com.typesafe.scalalogging.LazyLogging
import protocol2.resource.ResourceManager

import scala.language.reflectiveCalls
import scala.util.{Failure, Success}

class SimpleObjectSocket(connection: ResourceManager[ObjectSocketConnection]) extends ObjectSocket with LazyLogging {

  def send[A](message: A): Either[Iterable[Throwable], Unit] = {
    val res = connection.get match {
      case Success(conn) => conn.write(message) match {
        case Success(_) => Right(())
        case Failure(ex) => Left(connection.close().toList :+ ex)
      }
      case Failure(ex) => Left(List(ex))
    }

    logger.info(s"SEND [$message] -> [$res]")

    res
  }

  def receive[A]: Either[Iterable[Throwable], A] = {
    val res = connection.get match {
      case Success(conn) => conn.read[A] match {
        case Success(r) => Right(r)
        case Failure(ex) => Left(connection.close().toList :+ ex)
      }
      case Failure(ex) => Left(List(ex))
    }

    logger.info(s"RECV -> [$res]")
    res
  }

  def close: Iterable[Throwable] = connection.close()

}
