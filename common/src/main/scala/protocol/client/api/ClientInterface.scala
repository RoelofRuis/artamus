package protocol.client.api

import protocol.Exceptions.CommunicationException

trait ClientInterface[C, Q <: { type Res }] {

  def sendCommand(command: C): Option[CommunicationException]

  def sendQuery[A <: Q](query: A): Either[CommunicationException, A#Res]

}
