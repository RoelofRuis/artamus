package server.domain

import javax.inject.Inject
import music.domain.track.Track
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.Request
import server.analysis.blackboard.Controller
import server.domain.track.Savepoint
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  changeCommands: Dispatcher[Request, Command],
  eventBus: EventBus[Event],
  interpreter: LilypondInterpreter,
  savepoint: Savepoint,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  changeCommands.subscribe[Analyse.type] { req =>
    eventBus.publish(AnalysisStarted)
    val analysedTrack = analysis.run(req.user.workspace.editedTrack)
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)
    savepoint.writeStaged(analysedTrack)
    true
  }

  changeCommands.subscribe[Commit.type] { _ =>
    savepoint.commit()
    true
  }

  changeCommands.subscribe[Rollback.type] { req: Request[Rollback.type] =>
    savepoint.rollback()
    changeCommands.handle(Request(req.user, Analyse)) // TODO: probably split analyse and handle
    true
  }

}


