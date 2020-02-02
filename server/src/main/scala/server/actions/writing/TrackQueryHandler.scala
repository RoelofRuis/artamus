package server.actions.writing

import api.Query
import api.Write.Perform
import domain.write.Track
import javax.inject.{Inject, Singleton}
import server.Request
import server.actions.Responses
import server.infra.ServerDispatcher

import scala.util.Try

@Singleton
private[server] class TrackQueryHandler @Inject() (
  dispatcher: ServerDispatcher
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[Perform.type]{ req =>
    import domain.perform._

    readTrack(req, Interpretation.perform, TrackPerformance())
  }

  def readTrack[A](req: Request[Query], f: Track => A, onNotFound: => A): Try[A] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.selectedTrack)
    } yield f(track)

    Responses.returning[A](res, Some(onNotFound))
  }

}
