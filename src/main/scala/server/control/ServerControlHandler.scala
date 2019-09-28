package server.control

import javax.inject.Inject
import protocol.{Command, ServerInterface}
import pubsub.Dispatcher

private[server] class ServerControlHandler @Inject() (
  server: ServerInterface,
  dispatcher: Dispatcher[Command]
) {

  dispatcher.subscribe[Disconnect] {
    case Disconnect(false) =>
      true

    case Disconnect(true) =>
      initiateShutdown()
      true

    case _ => false
  }

  private def initiateShutdown(): Unit = {
    new Thread (() => server.shutdown()).start()
  }

}


