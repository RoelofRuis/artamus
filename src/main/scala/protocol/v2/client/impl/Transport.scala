package protocol.v2.client.impl

import protocol.v2.Exceptions.ResponseException

trait Transport {

  def send[A, B](request: A): Either[ResponseException, B]

}
