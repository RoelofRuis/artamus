import protocol.client.DefaultClient
import pubsub.{Dispatcher, SimpleDispatcher, Subscriber}

import scala.language.reflectiveCalls
import scala.util.Try

package object protocol {

  def createClient(port: Int, bindings: ClientBindings): ClientInterface = new DefaultClient(port, bindings)

  def createDispatcher[A <: { type Res }](): Dispatcher[A] = new SimpleDispatcher[A]

  trait ClientInterface {
    def sendCommand[A <: Command](message: A): Option[Command#Res]

    def sendQuery[A <: Query](message: A): Option[A#Res]

    def close(): Unit
  }

  final case class ClientBindings(eventDispatcher: Dispatcher[Event])

  // TODO: clean this up!
  final case class ServerBindings(
    commandDispatcher: Dispatcher[Command],
    queryDispatcher: Dispatcher[Query],
    eventSubscriber: Subscriber[String, Event, Unit]
  ) {

    def subscribe(connectionId: String): Unit = {
      eventSubscriber.subscribe(connectionId, EventResponse(_))
    }

    def unsubscribe(connectionId: String): Unit = {
      eventSubscriber.unsubscribe(connectionId)
    }

    def handleRequest(request: Object): DataResponse = {
      val result = tryRead[ServerRequest](request).toEither match {
        case Right(CommandRequest(command)) =>
          commandDispatcher.handle(command) match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for command [$command]")
          }

        case Right(QueryRequest(query)) =>
          queryDispatcher.handle(query) match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for query [$query]")
          }

        case Left(ex) => Left(s"Unable to determine message type. [$ex]")
      }
      DataResponse(result)
    }

    private def tryRead[A](obj: Object): Try[A] = Try { obj.asInstanceOf[A] }
  }

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
