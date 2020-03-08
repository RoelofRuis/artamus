package server.actions.control

import domain.interact.Control.Disconnect
import javax.inject.{Inject, Singleton}
import server.async.{CommandHandlerRegistration, CommandRequest}

@Singleton
private[server] class ControlHandler @Inject() (
  registry: CommandHandlerRegistration
) {

  registry.register[Disconnect] { _ => CommandRequest.ok }

}


