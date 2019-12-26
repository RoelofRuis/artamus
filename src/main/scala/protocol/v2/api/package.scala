package protocol.v2

import protocol.Command

package object api {

  trait Command2 { final type Res = Unit }
  trait Query2 { type Res }
  trait Event2 { final type Res = Unit }

  sealed trait Request2
  final case class CommandRequest2(data: Command) extends Request2
  final case class QueryRequest2(data: Query2) extends Request2

  sealed trait Response2
  final case class DataResponse2(data: Either[ResponseException, Any]) extends Response2
  final case class EventResponse2[A <: Event2](event: A) extends Response2



  sealed trait ConnectionEvent extends Event2



  /** Any exception that caused incorrect server response */
  sealed trait ResponseException extends Exception
  /** Any error caused by server logic */
  final case class ServerException(cause: Throwable) extends ResponseException
  /** Any error caused by malfunctioning transport */
  sealed trait TransportException extends ResponseException
  final case object NotConnected extends TransportException

}
