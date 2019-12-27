package protocol.client.api

import protocol.{Command, Query}
import protocol.Exceptions.ResponseException

trait ClientInterface2 {

  def sendCommand[A <: Command](command: A): Option[ResponseException]

  def sendQuery[A <: Query](query: A): Either[ResponseException, A#Res]

}
