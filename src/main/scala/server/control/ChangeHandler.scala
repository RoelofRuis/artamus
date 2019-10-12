package server.control

import server.analysis.blackboard.Controller
import javax.inject.Inject
import music.collection.Track
import protocol.Command
import pubsub.Dispatcher
import server.domain.track.TrackState
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  busCommands: Dispatcher[Command],
  interpreter: LilypondInterpreter,
  state: TrackState,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  busCommands.subscribe[Commit.type] { _ =>
    val analysedTrack = analysis.run(state.readState)
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)
    true
  }

}


