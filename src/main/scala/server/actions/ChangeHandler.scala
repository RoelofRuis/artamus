package server.actions

import javax.inject.Inject
import music.model.write.track.Track
import protocol.{Command, Event}
import pubsub.{Dispatcher, EventBus}
import server.Request
import server.analysis.blackboard.Controller
import server.rendering.AsyncRenderer

private[server] class ChangeHandler @Inject() (
  dispatcher: Dispatcher[Request, Command],
  eventBus: EventBus[Event],
  analysis: Controller[Track],
  renderer: AsyncRenderer,
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[Analyse.type] { req =>
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.selectedTrack)
      _ = eventBus.publish(AnalysisStarted)
      analysedTrack = analysis.run(track)
      _ <- req.db.saveTrack(analysedTrack)
      _ = renderer.render(analysedTrack)
    } yield ()

    res.toTry
  }

}


