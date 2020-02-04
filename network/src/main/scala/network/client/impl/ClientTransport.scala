package network.client.impl

import network.Exceptions.CommunicationException

private[client] trait ClientTransport {

  def send[A, B](request: A): Either[CommunicationException, B]

}
