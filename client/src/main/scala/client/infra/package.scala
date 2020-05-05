package client

import client.infra.Callback
import nl.roelofruis.artamus.core.api.{Event, Request}
import network.client.api.ClientInterface
import nl.roelofruis.pubsub.Dispatcher

package object infra {

  type ClientDispatcher = Dispatcher[Callback, Event]
  type Client = ClientInterface[Request]

}
