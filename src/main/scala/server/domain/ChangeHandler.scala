package server.domain

import javax.inject.Inject
import music.symbol.collection.Track
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.analysis.blackboard.Controller
import server.domain.track.TrackState
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  changeCommands: Dispatcher[Command],
  eventBus: EventBus[Event],
  interpreter: LilypondInterpreter,
  state: TrackState,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  changeCommands.subscribe[Analyse.type] { _ =>
    eventBus.publish(AnalysisStarted)
    val analysedTrack = analysis.run(state.getEditable)
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)
    state.stage(analysedTrack)
    true
  }

  changeCommands.subscribe[Commit.type] { _ =>
    state.commit()
    true
  }

  changeCommands.subscribe[Rollback.type] { _ =>
    state.rollback()
    changeCommands.handle(Analyse) // TODO: probably split analyse and handle
    true
  }

}


