package server.control

import javax.inject.Inject
import protocol.{Command, Event, Query}
import pubsub.{BufferedEventBus, Dispatcher}

private[server] class EventBusHandler @Inject() (
  busCommands: Dispatcher[Command],
  busQueries: Dispatcher[Query],
  eventBus: BufferedEventBus[Event]
) {

  busCommands.subscribe[PublishChanges.type] { _ =>
    val numFlushed = eventBus.flush
    numFlushed > 0
  }

  busQueries.subscribe[GetViews.type] { _ =>
    eventBus.viewSubscriptions.toList
  }

}


