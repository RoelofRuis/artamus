package server.actions.control

import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.{Request, Responses}

private[server] class ServerControlHandler @Inject() (
  dispatcher: Dispatcher[Request, Command]
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, _, Disconnect()) => Responses.ok
  }

}


