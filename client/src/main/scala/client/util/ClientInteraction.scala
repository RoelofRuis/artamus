package client.util

import client.Client
import com.typesafe.scalalogging.LazyLogging
import domain.interact.{Command, Query}
import network.Exceptions.CommunicationException

object ClientInteraction {

  implicit class CommandQueryClient(client: Client) extends LazyLogging {
    def sendCommand[A <: Command](command: A): Option[CommunicationException] = {
      client.send(command) match {
        case Right(_) =>
          logger.debug(s"Command [$command] executed")
          None
        case Left(ex) =>
          logger.error(s"Command [$command] failed", ex)
          ex.cause.foreach(logger.warn(s"Underlying error", _))
          Some(ex)
      }
    }

    def sendQuery[A <: Query](query: A): Either[CommunicationException, A#Res] = {
      client.send(query) match {
        case Right(res) =>
          logger.debug(s"Query [$query] executed")
          Right(res)
        case Left(ex) =>
          logger.error(s"Query [$query] failed", ex)
          Left(ex)
      }
    }
  }

}
