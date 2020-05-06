package network.client.api

import network.Exceptions.CommunicationException

trait ClientInterface[R <: { type Res }] {

  def send[A <: R](request: A): Either[CommunicationException, A#Res]

}
