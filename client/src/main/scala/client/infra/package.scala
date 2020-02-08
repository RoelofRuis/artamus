package client

import domain.interact.{Event, Request}
import network.client.api.ClientInterface
import pubsub.{Callback, Dispatcher}

package object infra {

  type ClientDispatcher = Dispatcher[Callback, Event]
  type Client = ClientInterface[Request]

}
