package protocol.client.impl

import protocol.Exceptions.CommunicationException

private[client] trait ClientTransport {

  def send[A, B](request: A): Either[CommunicationException, B]

}
