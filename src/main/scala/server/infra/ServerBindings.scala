package server.infra

import javax.inject.Inject
import protocol.{Command, Event, Query}
import pubsub.{Dispatcher, EventBus}
import server.Request

import scala.util.Try

final class ServerBindings @Inject() (
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

  def handleCommand(request: Request[Command]): Try[Unit] = commandDispatcher.handle(request)

  def handleQuery(request: Request[Query]): Try[Query#Res] = queryDispatcher.handle(request)

}
