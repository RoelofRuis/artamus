package protocol2

import com.typesafe.scalalogging.LazyLogging

import scala.language.reflectiveCalls
import scala.util.{Failure, Success}

private[protocol] class SimpleObjectSocket(manager: ConnectionManager) extends ObjectSocket with LazyLogging {

  def send[A](message: A): Option[String] = {
    val res = manager.getConnection.flatMap { conn =>
      conn.write(message) match {
        case Success(_) => Right(Unit)
        case Failure(ex) =>
          val closeConnMessage = manager
            .closeConnection()
            .fold("Closed connection")(ex => s"Failed to close connection [$ex]")
          Left(s"Error when sending message [$ex]\n$closeConnMessage")
      }
    }
    logger.info(s"SEND [$message] -> [$res]")
    res.left.toOption
  }

  def receive[A]: Either[String, A] = {
    val res = manager.getConnection.flatMap { conn =>
      conn.read[A] match {
        case Success(response) => Right(response)
        case Failure(ex) =>
          val closeConnMessage = manager
            .closeConnection()
            .fold("Closed connection")(ex => s"Failed to close connection [$ex]")
          Left(s"Error when retrieving message [$ex]\n$closeConnMessage")
      }
    }
    logger.info(s"RECV -> [$res]")
    res
  }

  def close: Option[Throwable] = manager.closeConnection()

}
