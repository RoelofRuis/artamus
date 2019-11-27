package server.domain

import javax.inject.Inject
import music.domain.track.{Track, TrackRepository}
import music.domain.workspace.WorkspaceRepository
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.Request
import server.analysis.blackboard.Controller
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  trackRepo: TrackRepository,
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
      track <- workspace.editedTrack.flatMap(trackRepo.getById).getOrElse(trackRepo.put(Track()))
      analysedTrack = analysis.run(track)
      lilypondFile = interpreter.interpret(analysedTrack)
      _ = renderer.submit("committed-changes", lilypondFile)
      editedWorkspace = workspace.setTrackToAnnotate(analysedTrack)
      _ <- trackRepo.put(analysedTrack)
      _ <- workspaceRepo.put(editedWorkspace)
    } yield true
  }

  changeCommands.subscribe[Commit.type] { req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
      _ <- workspaceRepo.put(workspace.useAnnotations)
    } yield true
  }

}


