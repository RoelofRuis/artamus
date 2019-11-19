package server

import javax.inject.Inject
import protocol.{Command, Event, EventResponse, Query, ServerException}
import pubsub.{Dispatcher, EventBus}

import scala.util.{Failure, Success, Try}

class ServerBindings @Inject() (
  commandDispatcher: Dispatcher[Command],
  queryDispatcher: Dispatcher[Query],
  eventSubscriber: EventBus[Event]
) {

  def subscribeEvents(subscribeKey: String, callback: Any => Unit): Unit = {
    eventSubscriber.subscribe(subscribeKey, event => callback(EventResponse(event)))
  }

  def unsubscribeEvents(subscribeKey: String): Unit = {
    eventSubscriber.unsubscribe(subscribeKey)
  }

  def handleCommand(command: Command): Either[ServerException, Command#Res] = {
    Try { commandDispatcher.handle(command) } match {
      case Success(response) => response match {
        case Some(res) => Right(res)
        case None => Left(s"No handler defined for command [$command]")
      }
      case Failure(ex) => Left(s"Error during command execution [$ex]")
    }
  }

  def handleQuery(query: Query): Either[ServerException, Query#Res] = {
    Try { queryDispatcher.handle(query) } match {
      case Success(response) => response match {
        case Some(res) => Right(res)
        case None => Left(s"No handler defined for query [$query]")
      }
      case Failure(ex) => Left(s"Error during query execution [$ex]")
    }
  }

}
