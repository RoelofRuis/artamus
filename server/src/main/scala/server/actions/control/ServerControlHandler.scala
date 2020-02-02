package server.actions.control

import api.Control.Disconnect
import javax.inject.{Inject, Singleton}
import server.Request
import server.actions.Responses
import server.infra.ServerDispatcher

@Singleton
private[server] class ServerControlHandler @Inject() (
  dispatcher: ServerDispatcher
) {

  dispatcher.subscribe[Disconnect] {
    case Request(_, _, Disconnect()) => Responses.ok
  }

}


