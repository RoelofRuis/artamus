package server.actions.control

import domain.interact.Control.Disconnect
import javax.inject.{Inject, Singleton}
import server.ServerRequest
import server.actions.Responses
import server.infra.ServerDispatcher

@Singleton
private[server] class ServerControlHandler @Inject() (
  dispatcher: ServerDispatcher
) {

  dispatcher.subscribe[Disconnect] {
    case ServerRequest(_, _, Disconnect()) => Responses.ok
  }

}


