package server

import javax.inject.Inject
import protocol.{Command, Event, Query, ServerException}
import pubsub.{Dispatcher, EventBus}

import scala.util.{Failure, Success}

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
    commandDispatcher.handle(request) match {
      case Success(response) => Right(response)
      case Failure(ex) => Left(s"Error during command execution [$ex]")
    }
  }

  def handleQuery(request: Request[Query]): Either[ServerException, Query#Res] = {
    queryDispatcher.handle(request) match {
      case Success(response) => Right(response)
      case Failure(ex) => Left(s"Error during query execution [$ex]")
    }
  }

}
