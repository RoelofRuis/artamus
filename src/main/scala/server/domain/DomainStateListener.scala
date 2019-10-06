package server.domain

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.Inject
import pubsub.BufferedEventBus
import server.analysis.RenderingAnalysis
import server.rendering.LilypondRenderer

@NotThreadSafe
class DomainStateListener @Inject() (
  domainUpdates: BufferedEventBus[DomainEvent],
  renderingAnalysis: RenderingAnalysis,
  rendering: LilypondRenderer,
) {

  domainUpdates.subscribe("domain-state-rendering", {
    case StateChanged(state) =>
      val lilyFile = renderingAnalysis.toLilypondFile("domain", state)
      rendering.submit("melodic-analysis", lilyFile)
    case _ => ()
  })
}
