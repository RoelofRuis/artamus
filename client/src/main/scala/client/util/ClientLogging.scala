package client.util

import api.{Command, Query}
import client.Client
import com.typesafe.scalalogging.LazyLogging
import protocol.Exceptions.CommunicationException

object ClientLogging {

  implicit class LoggedClientOps(client: Client) extends LazyLogging {
    def sendCommandLogged[A <: Command](command: A): Option[CommunicationException] = {
      client.sendCommand(command) match {
        case None =>
          logger.debug(s"Command [$command] executed")
          None
        case Some(ex) =>
          logger.error(s"Command [$command] failed", ex)
          ex.cause.foreach(logger.warn(s"Underlying error", _))
          Some(ex)
      }
    }

    def sendQueryLogged[A <: Query](query: A): Either[CommunicationException, A#Res] = {
      client.sendQuery(query) match {
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
