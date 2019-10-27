package server

import protocol.transport.server.ServerBindings
import protocol._
import pubsub.{Dispatcher, Subscriber}
import server.domain.Commit

import scala.util.{Failure, Success, Try}

final case class ProtocolServerBindings(
  commandDispatcher: Dispatcher[Command],
  queryDispatcher: Dispatcher[Query],
  eventSubscriber: Subscriber[String, Event, Unit]
) extends ServerBindings {

  def connectionAccepted(connectionId: String, callback: Any => Unit): Unit = {
    eventSubscriber.subscribe(connectionId, event => callback(EventResponse(event)))
    Try { commandDispatcher.handle(Commit) } // TODO: remove hard coded first render when server acceptance logic is improved
  }

  def connectionDropped(connectionId: String): Unit = {
    eventSubscriber.unsubscribe(connectionId)
  }

  def handleRequest(request: Object): DataResponse = {
    val result = tryRead[ServerRequest](request).toEither match {
      case Right(CommandRequest(command)) =>
        Try { commandDispatcher.handle(command) } match {
          case Success(response) => response match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for command [$command]")
          }
          case Failure(ex) => Left(s"Error during command execution [$ex]")
        }

      case Right(QueryRequest(query)) =>
        Try { queryDispatcher.handle(query) } match {
          case Success(response) => response match {
            case Some(res) => Right(res)
            case None => Left(s"No handler defined for query [$query]")
          }
          case Failure(ex) => Left(s"Error during query execution [$ex]")
        }


      case Left(ex) => Left(s"Unable to determine message type. [$ex]")
    }
    DataResponse(result)
  }

  private def tryRead[A](obj: Object): Try[A] = Try { obj.asInstanceOf[A] }
}
