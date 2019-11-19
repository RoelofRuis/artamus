package server

import javax.inject.Inject
import protocol.{Command, Event, EventResponse, Query}
import pubsub.{Dispatcher, EventBus}

class ServerBindings @Inject() (
  commandDispatcher: Dispatcher[Command],
  queryDispatcher: Dispatcher[Query],
  eventSubscriber: EventBus[Event]
) {

  def subscribeEvents(connectionId: String, callback: Any => Unit): Unit = {
    eventSubscriber.subscribe(connectionId, event => callback(EventResponse(event)))
  }

  def unsubscribeEvents(connectionId: String): Unit = {
    eventSubscriber.unsubscribe(connectionId)
  }

  def handleCommand(command: Command): Option[Command#Res] = {
    commandDispatcher.handle(command)
  }

  def handleQuery(query: Query): Option[Query#Res] = {
    queryDispatcher.handle(query)
  }

}
