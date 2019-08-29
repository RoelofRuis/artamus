package object protocol {

  sealed trait ServerRequestMessage
  case object ControlMessage extends ServerRequestMessage
  case object CommandMessage extends ServerRequestMessage

  sealed trait ServerResponseMessage
  case object ResponseMessage extends ServerResponseMessage
  case object EventMessage extends ServerResponseMessage

  trait Event

  trait Control

  trait Command

  trait Query {
    type Res
  }

}
