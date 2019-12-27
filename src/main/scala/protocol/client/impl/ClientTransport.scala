package protocol.client.impl

import protocol.Exceptions.CommunicationException

trait ClientTransport {

  def send[A, B](request: A): Either[CommunicationException, B]

}
