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
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      analysedTrack = analysis.run(workspace.editedTrack)
      lilypondFile = interpreter.interpret(analysedTrack)
      _ = renderer.submit("committed-changes", lilypondFile)
      _ <- workspaceRepo.put(workspace.makeAnnotations(analysedTrack))
    } yield true
  }

  changeCommands.subscribe[Commit.type] { req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      _ <- workspaceRepo.put(workspace.useAnnotations)
    } yield true
  }

}


