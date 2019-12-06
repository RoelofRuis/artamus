package server

import javax.inject.Inject
import protocol.{Command, Event, Query}
import pubsub.{Dispatcher, EventBus}

import scala.util.Try

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

  def handleCommand(request: Request[Command]): Try[Command#Res] = commandDispatcher.handle(request)

  def handleQuery(request: Request[Query]): Try[Query#Res] = queryDispatcher.handle(request)

}
