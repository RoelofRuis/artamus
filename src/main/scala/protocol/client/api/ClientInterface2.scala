package protocol.client.api

import protocol.{Command, Query}
import protocol.Exceptions.CommunicationException

trait ClientInterface2 {

  def sendCommand[A <: Command](command: A): Option[CommunicationException]

  def sendQuery[A <: Query](query: A): Either[CommunicationException, A#Res]

}
