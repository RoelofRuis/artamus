package server.control

import javax.inject.Inject
import protocol.Control
import protocol.server.ServerInterface
import pubsub.Dispatcher

private[server] class ServerControlHandler @Inject() (
  dispatcher: Dispatcher[Control],
  server: ServerInterface
) {

  dispatcher.subscribe[Disconnect] {
    case Disconnect(false) =>
      server.closeActiveConnection()
      true

    case Disconnect(true) =>
      server.stopServer()
      true

    case _ => false
  }

}


