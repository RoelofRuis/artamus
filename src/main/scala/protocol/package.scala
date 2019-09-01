import protocol.client.DefaultClient
import protocol.server.SingleConnectionServer

import scala.util.Try

package object protocol {

  def createClient(port: Int): Try[ClientInterface] = Try(new DefaultClient(port))
  def createServer(port: Int): ServerInterface = new SingleConnectionServer(port)

  trait Control

  trait Command

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
