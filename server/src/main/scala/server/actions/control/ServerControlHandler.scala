package server.actions.control

import api.Control.Disconnect
import javax.inject.{Inject, Singleton}
import protocol.Command
import pubsub.Dispatcher
import server.Request
import server.actions.Responses

@Singleton
private[server] class ServerControlHandler @Inject() (
  dispatcher: Dispatcher[Request, Command]
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, _, Disconnect()) => Responses.ok
  }

}


