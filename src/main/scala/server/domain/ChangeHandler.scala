package server.domain

import javax.inject.Inject
import music.domain.track.Track2
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.analysis.blackboard.Controller
import server.domain.track.Savepoint
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  changeCommands: Dispatcher[Command],
  eventBus: EventBus[Event],
  interpreter: LilypondInterpreter,
  savepoint: Savepoint,
  analysis: Controller[Track2],
  renderer: Renderer,
) {

  changeCommands.subscribe[Analyse.type] { _ =>
    eventBus.publish(AnalysisStarted)
    val analysedTrack = analysis.run(savepoint.getCurrentTrack)
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)
    savepoint.writeStaged(analysedTrack)
    true
  }

  changeCommands.subscribe[Commit.type] { _ =>
    savepoint.commit()
    true
  }

  changeCommands.subscribe[Rollback.type] { _ =>
    savepoint.rollback()
    changeCommands.handle(Analyse) // TODO: probably split analyse and handle
    true
  }

}


