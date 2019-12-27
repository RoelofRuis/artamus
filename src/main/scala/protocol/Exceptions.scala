package protocol

object Exceptions {

  /** Any exception that caused incorrect server response */
  sealed trait ResponseException extends Exception // TODO: maybe rename to ProtocolException

  /** Any error caused by the server */
  sealed trait ServerException extends ResponseException

  final case object Unauthorized extends ServerException
  final case class InvalidRequest(message: String) extends ServerException

  final case object MessageException extends ServerException
  final case object InvalidStateException extends ServerException
  final case object StorageException extends ServerException
  final case object LogicException extends ServerException


  /** Any error caused by malfunctioning transport */
  sealed trait TransportException extends ResponseException

  /** The server sent an unexpected response  */
  final case object UnexpectedResponse extends TransportException

  /** The client is not connected to the server */
  final case object NotConnected extends TransportException

  /** Any exception when trying to set up the connection to the server */
  final case class ConnectException(cause: Throwable) extends TransportException

  /** There was an error receiving a message */
  final case class ClientReceiveException(cause: Throwable) extends TransportException

  /** Any exception when writing data to the server connection */
  final case class WriteException(cause: Throwable) extends TransportException

  /** Any exception when reading data from the server connection */
  final case class ReadException(cause: Throwable) extends TransportException

}
