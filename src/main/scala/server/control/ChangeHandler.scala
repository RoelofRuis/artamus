package server.control

import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.analysis.RenderingController
import server.domain.track.TrackState
import server.rendering.LilypondRenderer

private[server] class ChangeHandler @Inject() (
  busCommands: Dispatcher[Command],
  state: TrackState,
  renderingController: RenderingController,
  rendering: LilypondRenderer,
) {

  busCommands.subscribe[CommitChanges.type] { _ =>
    val lilyFile = renderingController.run(state.readState)
    rendering.submit("committed-changes", lilyFile)
    true
  }

}


