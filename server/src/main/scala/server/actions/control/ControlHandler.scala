package server.actions.control

import domain.interact.Control.Disconnect
import javax.inject.{Inject, Singleton}
import server.async.{ActionRegistration, ActionRequest}

@Singleton
private[server] class ControlHandler @Inject() (
  registry: ActionRegistration
) {

  registry.register[Disconnect] { _ => ActionRequest.ok }

}


