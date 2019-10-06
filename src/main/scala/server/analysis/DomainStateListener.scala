package server.analysis

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import pubsub.BufferedEventBus
import server.domain.{DomainEvent, StateChanged}

@NotThreadSafe
class DomainStateListener @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  renderingController: RenderingController
) {

  domainUpdates.subscribe("domain-state-rendering", {
    case StateChanged(state) =>
      renderingController.render("domain", state)
    case _ => ()
  })
}
