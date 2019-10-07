package server.control

import blackboard.Controller
import javax.inject.Inject
import protocol.Command
import pubsub.Dispatcher
import server.domain.track.TrackState
import server.domain.track.container.Track
import server.rendering.{LilypondInterpreter, LilypondRenderer}

private[server] class ChangeHandler @Inject() (
  busCommands: Dispatcher[Command],
  state: TrackState,
  analysis: Controller[Track],
  interpreter: LilypondInterpreter,
  rendering: LilypondRenderer,
) {

  busCommands.subscribe[CommitChanges.type] { _ =>
    val analysedTrack = analysis.run(state.readState)
    val lilyFile = interpreter.interpret(analysedTrack)
    rendering.submit("committed-changes", lilyFile)
    true
  }

}


