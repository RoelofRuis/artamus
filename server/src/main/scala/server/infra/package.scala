package server

import domain.interact.{Event, Query}
import pubsub.{Dispatcher, EventBus}
import server.async.QueryRequest

package object infra {

  type QueryDispatcher = Dispatcher[QueryRequest, Query]
  type ServerEventBus = EventBus[Event]

}
