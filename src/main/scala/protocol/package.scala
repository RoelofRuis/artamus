import protocol.Exceptions.ResponseException

package object protocol {

  trait Command { final type Res = Unit }
  trait Query { type Res }
  trait Event { final type Res = Unit }

  sealed trait RequestMessage
  final case class CommandMessage(command: Command) extends RequestMessage
  final case class QueryMessage(query: Query) extends RequestMessage

  sealed trait ResponseMessage
  final case class DataMessage(data: Either[ResponseException, Any]) extends ResponseMessage
  final case class EventMessage[A <: Event](event: A) extends ResponseMessage

}
