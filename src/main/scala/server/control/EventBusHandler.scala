package server.control

import javax.inject.Inject
import protocol.{Command, Query}
import pubsub.{BufferedEventBus, Dispatcher}
import server.domain.DomainEvent

private[server] class EventBusHandler @Inject() (
  busCommands: Dispatcher[Command],
  busQueries: Dispatcher[Query],
  domainUpdates: BufferedEventBus[DomainEvent]
) {

  busCommands.subscribe[PublishChanges.type] { _ =>
    val numFlushed = domainUpdates.flush
    numFlushed > 0
  }

  busQueries.subscribe[GetDomainListeners.type] { _ =>
    domainUpdates.viewSubscriptions.toList
  }

}


