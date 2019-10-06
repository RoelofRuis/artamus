package server.domain

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import pubsub.BufferedEventBus
import server.analysis.RenderingController
import server.rendering.LilypondRenderer

@NotThreadSafe
class DomainStateListener @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  renderingController: RenderingController,
  rendering: LilypondRenderer,
) {

  domainUpdates.subscribe("domain-state-rendering", {
    case StateChanged(state) =>
      val lilyFile = renderingController.run(state)
      rendering.submit("domain-state", lilyFile)
    case _ => ()
  })
}
