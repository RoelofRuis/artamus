package server.actions.control

import javax.inject.Inject
import protocol.{Command, ServerInterface}
import pubsub.Dispatcher
import server.{Request, Responses}

private[server] class ServerControlHandler @Inject() (
  server: ServerInterface,
  dispatcher: Dispatcher[Request, Command]
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, _, Disconnect(false)) => Responses.ok
    case Request(_, _, Disconnect(true)) =>
      server.shutdown()
      Responses.ok

    case _ => Responses.ok
  }

}


