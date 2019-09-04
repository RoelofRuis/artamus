import protocol.ServerInterface.Dispatcher
import protocol.client.DefaultClient
import protocol.server.{SimpleDispatcher, SingleConnectionServer}

import scala.util.Try

package object protocol {

  def createClient(port: Int): Try[ClientInterface] = Try(new DefaultClient(port))

  def createServer(port: Int): ServerInterface = new SingleConnectionServer(port)
  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new SimpleDispatcher[A]

  trait Control { final type Res = Boolean }

  trait Command { final type Res = Boolean }

  trait Query { type Res }

  trait Event

  private[protocol] object MessageTypes {

    sealed trait ServerRequest
    case object ControlRequest extends ServerRequest
    case object CommandRequest extends ServerRequest
    case object QueryRequest extends ServerRequest

    sealed trait ServerResponse
    case object DataResponse extends ServerResponse
    case object EventResponse extends ServerResponse

    type StreamException = String

  }

}
