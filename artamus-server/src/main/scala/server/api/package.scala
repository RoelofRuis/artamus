package server

import artamus.core.api.{Event, Query}
import nl.roelofruis.pubsub.{Dispatcher, EventBus}

package object api {

  type QueryDispatcher = Dispatcher[QueryRequest, Query]
  type ServerEventBus = EventBus[Event]

}
