package server.actions.writing

import javax.inject.{Inject, Singleton}
import music.model.write.Track
import protocol.Query
import pubsub.Dispatcher
import server.Request
import server.actions.Responses

import scala.util.Try

@Singleton
private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query]
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[Perform.type]{ req =>
    import music.model.perform._

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
