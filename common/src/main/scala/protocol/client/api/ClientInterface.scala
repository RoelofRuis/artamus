package protocol.client.api

import protocol.Exceptions.CommunicationException

trait ClientInterface[R <: { type Res }] {

  def send[A <: R](request: A): Either[CommunicationException, A#Res]

}
