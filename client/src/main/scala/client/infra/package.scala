package client

import client.infra.Callback
import domain.interact.{Event, Request}
import network.client.api.ClientInterface
import pubsub.Dispatcher

package object infra {

  type ClientDispatcher = Dispatcher[Callback, Event]
  type Client = ClientInterface[Request]

}
