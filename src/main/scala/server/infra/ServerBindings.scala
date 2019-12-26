package server.infra

import javax.inject.Inject
import protocol.v2.{Command2, Event2, Query2}
import pubsub.{Dispatcher, EventBus}
import server.Request

import scala.util.Try

final class ServerBindings @Inject() (
  commandDispatcher: Dispatcher[Request, Command2],
  queryDispatcher: Dispatcher[Request, Query2],
  eventSubscriber: EventBus[Event2]
) {

  def subscribeEvents(subscribeKey: String, callback: Event2 => Unit): Unit = {
    eventSubscriber.subscribe(subscribeKey, callback)
  }

  def unsubscribeEvents(subscribeKey: String): Unit = {
    eventSubscriber.unsubscribe(subscribeKey)
  }

  def handleCommand(request: Request[Command2]): Try[Unit] = commandDispatcher.handle(request)

  def handleQuery(request: Request[Query2]): Try[Query2#Res] = queryDispatcher.handle(request)

}
