package protocol

object Exceptions {

  sealed trait CommunicationException {
    val name: String
    val description: String
    val cause: Option[Throwable]
  }

  sealed trait ResponseException extends CommunicationException {
    val cause: Option[Throwable] = None
  }

  final case object Unauthenticated extends ResponseException {
    val name = "Unauthenticated"
    val description = "There is no user authenticated for the request"
  }

  final case class InvalidParameters(message: String) extends ResponseException {
    val name = "Invalid Parameters"
    val description = s"Invalid parameters in request: [$message]"
  }

  final case object InvalidMessage extends ResponseException {
    val name = "Invalid Message"
    val description = "The message could not be read"
  }

  final case object LogicError extends ResponseException {
    val name = "Logic Error"
    val description = "The server encountered an unexpected error"
  }

  final case object InvalidStateError extends ResponseException {
    val name = "Invalid State"
    val description = " The sever encountered an invalid state (this is really bad!)"
  }

  sealed trait TransportException extends CommunicationException

  final case object UnexpectedDataResponse extends TransportException {
    val name = "Unexpected Data"
    val description = "A data response was received when none was expected"
    val cause: Option[Throwable] = None
  }

  final case object NotConnected extends TransportException {
    val name = "Not Connected"
    val description = "There is no active connection and none could be made"
    val cause: Option[Throwable] = None
  }

  final case class ConnectionException(err: Throwable) extends TransportException {
    val name = "Connection Exception"
    val description = "There was a problem when creating the connection"
    val cause: Option[Throwable] = Some(err)
  }

  final case class WriteException(err: Throwable) extends TransportException {
    val name = "Write Exception"
    val description = "There was a problem writing data"
    val cause: Option[Throwable] = Some(err)
  }

  final case class ReadException(err: Throwable) extends TransportException {
    val name = "Read Exception"
    val description = "There was a problem reading data"
    val cause: Option[Throwable] = Some(err)
  }

}
