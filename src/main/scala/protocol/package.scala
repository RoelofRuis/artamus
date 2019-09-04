import protocol.client.{ClientInterface, DefaultClient}
import protocol.server.{ServerInterface, SingleConnectionServer}

import scala.language.reflectiveCalls
import scala.reflect.ClassTag
import scala.util.Try

package object protocol {

  def createClient(port: Int): Try[ClientInterface] = Try(new DefaultClient(port))

  def createServer(port: Int): ServerInterface = new SingleConnectionServer(port)

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new Dispatchers.SimpleDispatcher[A]

  trait Control { final type Res = Boolean }

  trait Command { final type Res = Boolean }

  trait Query { type Res }

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
