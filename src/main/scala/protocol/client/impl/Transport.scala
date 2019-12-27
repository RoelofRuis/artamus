package protocol.client.impl

import protocol.Exceptions.CommunicationException

trait Transport {

  def send[A, B](request: A): Either[CommunicationException, B]

}
