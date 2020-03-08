package server.actions.perform

import domain.interact.Perform.PreparePerformance
import domain.interact.Query
import domain.write.Track
import javax.inject.{Inject, Singleton}
import server.infra.{QueryDispatcher, QueryRequest}

import scala.util.Try

@Singleton
private[server] class PerformanceQueryHandler @Inject() (
  dispatcher: QueryDispatcher
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[PreparePerformance.type]{ req =>
    import domain.perform._

    readTrack(req, Interpretation.perform, TrackPerformance())
  }

  def readTrack[A](req: QueryRequest[Query], f: Track => A, onNotFound: => A): Try[A] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
    } yield f(track)

    QueryRequest.returning[A](res, Some(onNotFound))
  }

}
