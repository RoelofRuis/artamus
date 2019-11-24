package server.control

import javax.inject.Inject
import protocol.{Command, ServerInterface}
import pubsub.Dispatcher
import server.Request

private[server] class ServerControlHandler @Inject() (
  server: ServerInterface,
  dispatcher: Dispatcher[Request, Command]
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, Disconnect(false)) =>
      true

    case Request(_, Disconnect(true)) =>
      server.shutdown()
      true

    case _ => false
  }

}


