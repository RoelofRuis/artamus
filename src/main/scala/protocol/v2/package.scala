package protocol

import protocol.v2.Exceptions.ResponseException

package object v2 {

  trait Command2 { final type Res = Unit }
  trait Query2 { type Res }
  trait Event2 { final type Res = Unit }

  sealed trait Request2
  final case class CommandRequest2(data: Command) extends Request2
  final case class QueryRequest2(data: Query2) extends Request2

  sealed trait Response2
  final case class DataResponse2(data: Either[ResponseException, Any]) extends Response2
  final case class EventResponse2[A <: Event2](event: A) extends Response2

}
