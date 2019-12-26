package protocol.v2.client.api

import protocol.v2.Exceptions.ResponseException
import protocol.v2.{Command2, Query2}

trait ClientInterface2 {

  def sendCommand[A <: Command2](command: A): Option[ResponseException]

  def sendQuery[A <: Query2](query: A): Either[ResponseException, A#Res]

}
