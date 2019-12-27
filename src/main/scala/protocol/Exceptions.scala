package protocol

object Exceptions {

  sealed trait CommunicationException extends Exception

  sealed trait ResponseException extends CommunicationException
  /** There is no user authenticated for the request */
  final case object Unauthenticated extends ResponseException
  /** The message contains invalid parameters */
  final case class InvalidParameters(message: String) extends ResponseException
  /** The message could not be read */
  final case object InvalidMessage extends ResponseException
  /** There was an unexpected logic error */
  final case object LogicError extends ResponseException
  /** The sever encountered an invalid state (this is really bad!) */
  final case object InvalidStateError extends ResponseException


  sealed trait TransportException extends CommunicationException
  /** A data response was received when none was expected */
  final case object UnexpectedDataResponse extends TransportException
  /** There is no active connection and none could be made */
  final case object NotConnected extends TransportException
  /** There was a problem when creating the connection */
  final case class ConnectException(cause: Throwable) extends TransportException
  /** There was a problem writing data */
  final case class WriteException(cause: Throwable) extends TransportException
  /** There was a problem reading data */
  final case class ReadException(cause: Throwable) extends TransportException

}
