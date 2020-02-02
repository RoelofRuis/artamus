package server

import api.{Event, Req}
import protocol.RequestMessage
import pubsub.{Dispatcher, EventBus}

package object infra {

  type ClientRequest = RequestMessage[Req]
  type ServerDispatcher = Dispatcher[Request, Req]
  type ServerEventBus = EventBus[Event]

}
