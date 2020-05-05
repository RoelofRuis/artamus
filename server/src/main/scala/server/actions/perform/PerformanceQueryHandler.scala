package server.actions.perform

import nl.roelofruis.artamus.core.api.Perform.PreparePerformance
import nl.roelofruis.artamus.core.api.Query
import nl.roelofruis.artamus.core.model.track.Track
import javax.inject.{Inject, Singleton}
import nl.roelofruis.artamus.core.ops.interpret.Interpretation
import server.api.{QueryDispatcher, QueryRequest}

import scala.util.Try

@Singleton
private[server] class PerformanceQueryHandler @Inject() (
  dispatcher: QueryDispatcher
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[PreparePerformance.type]{ req =>
    import nl.roelofruis.artamus.core.model.performance._

    readTrack(req, Interpretation.perform, Performance())
  }

  def readTrack[A](req: QueryRequest[Query], f: Track => A, onNotFound: => A): Try[A] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editingTrack)
    } yield f(track)

    QueryRequest.returning[A](res, Some(onNotFound))
  }

}
