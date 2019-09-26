import protocol.client.{ClientBindings, ClientInterface, DefaultClient}
import pubsub.{Dispatcher, SimpleDispatcher}

import scala.language.reflectiveCalls

package object protocol {

  def createClient(port: Int, bindings: ClientBindings): ClientInterface = new DefaultClient(port, bindings)

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new SimpleDispatcher[A]

  trait Message { type Res }
  trait Control extends Message { final type Res = Boolean }
  trait Command extends Message { final type Res = Boolean }
  trait Query extends Message
  trait Event { type Res = Unit }

  sealed trait ServerRequest
  final case class ControlRequest(data: Control) extends ServerRequest // TODO samenvoegen met command!
  final case class CommandRequest(data: Command) extends ServerRequest
  final case class QueryRequest(data: Query) extends ServerRequest

  sealed trait ServerResponse
  final case class DataResponse(data: Either[ServerException, Any]) extends ServerResponse
  final case class EventResponse[A <: Event](event: A) extends ServerResponse

  type ServerException = String

}
