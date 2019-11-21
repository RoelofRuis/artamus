package server

import javax.inject.Inject
import protocol.{Command, Event, Query, ServerException}
import pubsub.{Dispatcher, EventBus}

import scala.util.{Failure, Success, Try}

class ServerBindings @Inject() (
  commandDispatcher: Dispatcher[Request, Command],
  queryDispatcher: Dispatcher[Request, Query],
  eventSubscriber: EventBus[Event]
) {

  def subscribeEvents(subscribeKey: String, callback: Event => Unit): Unit = {
    eventSubscriber.subscribe(subscribeKey, callback)
  }

  def unsubscribeEvents(subscribeKey: String): Unit = {
    eventSubscriber.unsubscribe(subscribeKey)
  }

  def handleCommand(request: Request[Command]): Either[ServerException, Command#Res] = {
    Try { commandDispatcher.handle(request) } match {
      case Success(response) => response match {
        case Some(res) => Right(res)
        case None => Left(s"No handler defined for command [${request.attributes}]")
      }
      case Failure(ex) => Left(s"Error during command execution [$ex]")
    }
  }

  def handleQuery(request: Request[Query]): Either[ServerException, Query#Res] = {
    Try { queryDispatcher.handle(request) } match {
      case Success(response) => response match {
        case Some(res) => Right(res)
        case None => Left(s"No handler defined for query [${request.attributes}]")
      }
      case Failure(ex) => Left(s"Error during query execution [$ex]")
    }
  }

}
