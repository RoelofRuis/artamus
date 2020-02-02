import protocol.Exceptions.ResponseException

package object protocol {

  private[protocol] sealed trait ResponseMessage
  private[protocol] final case class DataResponseMessage(data: Either[ResponseException, Any]) extends ResponseMessage
  private[protocol] final case class EventResponseMessage[A](event: A) extends ResponseMessage

}
