import protocol.Exceptions.ResponseException

package object protocol {

  trait Command { final type Res = Unit }
  trait Query { type Res }
  trait Event { final type Res = Unit }

  sealed trait ServerRequest
  final case class CommandRequest(command: Command) extends ServerRequest
  final case class QueryRequest(query: Query) extends ServerRequest

  sealed trait ServerResponse
  final case class DataResponse(data: Either[ResponseException, Any]) extends ServerResponse
  final case class EventResponse[A <: Event](event: A) extends ServerResponse

}
