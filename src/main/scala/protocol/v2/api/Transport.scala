package protocol.v2.api

trait Transport {

  def send[A, B](request: A): Either[ResponseException, B]

}
