package server.domain

import javax.inject.Inject
import music.domain.track.Track
import music.domain.workspace.WorkspaceRepository
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.Request
import server.analysis.blackboard.Controller
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  changeCommands: Dispatcher[Request, Command],
  eventBus: EventBus[Event],
  interpreter: LilypondInterpreter,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  changeCommands.subscribe[Analyse.type] { req =>
    eventBus.publish(AnalysisStarted)
    val workspace = workspaceRepo.getByOwner(req.user)
    val analysedTrack = analysis.run(workspace.editedTrack)
    val lilypondFile = interpreter.interpret(analysedTrack)
    renderer.submit("committed-changes", lilypondFile)

    workspaceRepo.write(workspace.makeAnnotations(analysedTrack))
    true
  }

  changeCommands.subscribe[Commit.type] { req =>
    val workspace = workspaceRepo.getByOwner(req.user)

    workspaceRepo.write(workspace.useAnnotations)
    true
  }

}


