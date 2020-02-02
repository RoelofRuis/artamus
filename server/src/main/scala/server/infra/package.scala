package server

import api.{Event, Request}
import pubsub.{Dispatcher, EventBus}

package object infra {

  type ServerDispatcher = Dispatcher[ServerRequest, Request]
  type ServerEventBus = EventBus[Event]

}
