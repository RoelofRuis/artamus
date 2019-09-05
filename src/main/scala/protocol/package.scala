import protocol.client.{ClientBindings, ClientInterface, DefaultClient}
import protocol.server.{ServerInterface, SingleConnectionServer}

import scala.language.reflectiveCalls
import scala.reflect.ClassTag

package object protocol {

  def createClient(port: Int, bindings: ClientBindings): ClientInterface = new DefaultClient(port, bindings)

  def createServer(port: Int): ServerInterface = new SingleConnectionServer(port)

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new Dispatchers.SimpleDispatcher[A]

  trait Message { type Res }
  trait Control extends Message { final type Res = Boolean }
  trait Command extends Message { final type Res = Boolean }
  trait Query extends Message
  trait Event { type Res = Unit }

  trait Dispatcher[A <: { type Res }] {

    def handle[B <: A : ClassTag](msg: B): Option[B#Res]

    def subscribe[B <: A : ClassTag](f: B => B#Res): Unit

    def getSubscriptions: List[String]

  }

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
