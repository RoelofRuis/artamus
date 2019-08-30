package object protocol {

  private[protocol] sealed trait ServerRequestMessage
  private[protocol] case object ControlMessage extends ServerRequestMessage
  private[protocol] case object CommandMessage extends ServerRequestMessage

  private[protocol] sealed trait ServerResponseMessage
  private[protocol] case object ResponseMessage extends ServerResponseMessage
  private[protocol] case object EventMessage extends ServerResponseMessage

  // Public API
  trait Event

  trait Control

  trait Command

  trait Query {
    type Res
  }

  // TODO: implement creation functions for server and client!

}
