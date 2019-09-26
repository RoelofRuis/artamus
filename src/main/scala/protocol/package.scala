import protocol.client.{ClientBindings, ClientInterface, DefaultClient}
import pubsub.{Dispatcher, SimpleDispatcher}

import scala.language.reflectiveCalls

package object protocol {

  def createClient(port: Int, bindings: ClientBindings): ClientInterface = new DefaultClient(port, bindings)

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new SimpleDispatcher[A]

  trait Command { final type Res = Boolean }
  trait Query { type Res }
  trait Event { final type Res = Unit }

  sealed trait ServerRequest
  final case class CommandRequest(data: Command) extends ServerRequest
  final case class QueryRequest(data: Query) extends ServerRequest

  sealed trait ServerResponse
  final case class DataResponse(data: Either[ServerException, Any]) extends ServerResponse
  final case class EventResponse[A <: Event](event: A) extends ServerResponse

  type ServerException = String

}
