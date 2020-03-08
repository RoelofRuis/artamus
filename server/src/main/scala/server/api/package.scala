package server

import domain.interact.{Event, Query}
import pubsub.{Dispatcher, EventBus}

package object api {

  type QueryDispatcher = Dispatcher[QueryRequest, Query]
  type ServerEventBus = EventBus[Event]

}
