package object protocol {

  @deprecated
  trait Command { final type Res = Unit }
  @deprecated
  trait Query { type Res }
  @deprecated
  trait Event { final type Res = Unit }

  @deprecated
  sealed trait ServerRequest
  final case class CommandRequest(data: Command) extends ServerRequest
  final case class QueryRequest(data: Query) extends ServerRequest

  @deprecated
  sealed trait ServerResponse
  final case class DataResponse(data: Either[ServerException, Any]) extends ServerResponse
  final case class EventResponse[A <: Event](event: A) extends ServerResponse

  @deprecated
  type ServerException = String

}
