package server.domain

import javax.inject.Inject
import music.symbols.collection.Track
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.analysis.blackboard.Controller
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
    val analysedTrack = analysis.run(state.readState)
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)
    true
  }

}


