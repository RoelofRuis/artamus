package server.control

import blackboard.Controller
import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.domain.track.TrackState
import server.domain.track.container.Track
import server.rendering.{LilypondFile, LilypondRenderer}

private[server] class ChangeHandler @Inject() (
  busCommands: Dispatcher[Command],
  state: TrackState,
  renderingController: Controller[Track, LilypondFile],
  rendering: LilypondRenderer,
) {

  busCommands.subscribe[CommitChanges.type] { _ =>
    val lilyFile = renderingController.run(state.readState)
    rendering.submit("committed-changes", lilyFile)
    true
  }

}

