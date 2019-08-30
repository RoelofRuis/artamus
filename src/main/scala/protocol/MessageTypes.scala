package protocol

private[protocol] object MessageTypes {

  sealed trait ServerRequest
  case object ControlRequest extends ServerRequest
  case object CommandRequest extends ServerRequest
  case object QueryRequest extends ServerRequest

  sealed trait ServerResponse
  case object DataResponse extends ServerResponse
  case object ErrorResponse extends ServerResponse
  case object EventResponse extends ServerResponse

  type StreamException = String

}
