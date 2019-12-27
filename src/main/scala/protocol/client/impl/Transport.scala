package protocol.client.impl

import protocol.Exceptions.ResponseException

trait Transport {

  def send[A, B](request: A): Either[ResponseException, B]

}
