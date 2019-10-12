package server.control

import server.analysis.blackboard.Controller
import javax.inject.Inject
import music.collection.Track
import music.spelling.TrackSpelling
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.domain.track.TrackState
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  busCommands: Dispatcher[Command],
  eventBus: EventBus[Event],
  interpreter: LilypondInterpreter,
  state: TrackState,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  busCommands.subscribe[Commit.type] { _ =>
    eventBus.publish(ChangesCommitted)
    val analysedTrack = TrackSpelling(analysis.run(state.readState))
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)
    true
  }

}


