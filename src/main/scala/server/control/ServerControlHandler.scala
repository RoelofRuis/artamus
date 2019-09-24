package server.control

import javax.inject.Inject
import protocol.Control
import protocol.server.ServerInterface
import pubsub.Dispatcher

private[server] class ServerControlHandler @Inject() (
  server: ServerInterface,
  dispatcher: Dispatcher[Control]
) {

  dispatcher.subscribe[Disconnect] {
    case Disconnect(false) =>
      true

    case Disconnect(true) =>
      server.shutdown()
      true

    case _ => false
  }

}


