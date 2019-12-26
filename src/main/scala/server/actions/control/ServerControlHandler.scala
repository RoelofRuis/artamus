package server.actions.control

import javax.inject.Inject
import protocol.ServerInterface
import protocol.v2.Command2
import pubsub.Dispatcher
import server.{Request, Responses}

private[server] class ServerControlHandler @Inject() (
  server: ServerInterface,
  dispatcher: Dispatcher[Request, Command2]
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, _, Disconnect(false)) => Responses.ok
    case Request(_, _, Disconnect(true)) =>
      server.shutdown()
      Responses.ok

    case _ => Responses.ok
  }

}


