package server.control

import javax.inject.Inject
import protocol.{Event, Query}
import pubsub.{Dispatcher, EventBus}

private[server] class EventBusHandler @Inject() (
  dispatcher: Dispatcher[Query],
  eventBus: EventBus[Event]
) {

  dispatcher.subscribe[GetViews.type] { _ =>
    eventBus.viewSubscriptions.toList
  }

}


