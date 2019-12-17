package server.domain

import javax.inject.Inject
import music.domain.display.Display
import music.domain.write.track.Track
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.Request
import server.analysis.blackboard.Controller
import server.interpret.LilypondInterpreter
import server.rendering.Renderer

private[server] class ChangeHandler @Inject() (
  changeCommands: Dispatcher[Request, Command],
  eventBus: EventBus[Event],
  interpreter: LilypondInterpreter,
  analysis: Controller[Track],
  renderer: Renderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  changeCommands.subscribe[Analyse.type] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editedTrack)
      _ = eventBus.publish(AnalysisStarted)
      analysedTrack = analysis.run(track)
      displayTrack = Display.displayTrack(analysedTrack)
      lilypondFile = interpreter.interpret(displayTrack)
      _ = renderer.submit(workspace.editedTrack, lilypondFile)
      _ <- req.db.saveTrack(analysedTrack)
    } yield true

    res.toTry
  }

}


