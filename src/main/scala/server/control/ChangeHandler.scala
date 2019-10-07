package server.control

import blackboard.Controller
import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.domain.track.TrackState
import server.domain.track.container.Track
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  busCommands: Dispatcher[Command],
  state: TrackState,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  busCommands.subscribe[CommitChanges.type] { _ =>
    val analysedTrack = analysis.run(state.readState)
    renderer.submit("committed-changes", analysedTrack)
    true
  }

}


