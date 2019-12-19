package server.control

import javax.inject.Inject
import protocol.{Command, ServerInterface}
import pubsub.Dispatcher
import server.Request

import scala.util.Success

private[server] class ServerControlHandler @Inject() (
  server: ServerInterface,
  dispatcher: Dispatcher[Request, Command]
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, _, Disconnect(false)) =>
      Success(())

    case Request(_, _, Disconnect(true)) =>
      server.shutdown()
      Success(())

    case _ => Success(())
  }

}


