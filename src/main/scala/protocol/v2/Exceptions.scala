package protocol.v2

object Exceptions {

  /** Any exception that caused incorrect server response */
  sealed trait ResponseException extends Exception

  /** Any error caused by server logic */
  final case class ServerException(cause: Throwable) extends ResponseException

  /** The server sent an unexpected response  */
  final case object UnexpectedResponse extends ResponseException

  /** Any error caused by malfunctioning transport */
  sealed trait TransportException extends ResponseException

  /** The client is not connected to the server */
  final case object NotConnected extends TransportException

  /** Any exception when trying to set up the connection to the server */
  final case class ConnectException(cause: Throwable) extends TransportException

  /** Any exception when writing data to the server connection */
  final case class WriteException(cause: Throwable) extends TransportException

  /** Any exception when reading data from the server connection */
  final case class ReadException(cause: Throwable) extends TransportException

}
