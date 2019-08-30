package protocol

private[protocol] object MessageTypes {

  sealed trait ServerRequest
  case object ControlMessage extends ServerRequest
  case object CommandMessage extends ServerRequest

  sealed trait ServerResponse
  case object ResponseMessage extends ServerResponse
  case object EventMessage extends ServerResponse

}
