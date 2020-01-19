package server.actions.writing

import javax.inject.{Inject, Singleton}
import music.math.temporal.Position
import music.model.write.track.Track
import protocol.Query
import pubsub.Dispatcher
import server.{Request, Responses}

import scala.util.Try

@Singleton
private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query]
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[Perform.type]{ req =>
    import music.model.perform._

    readTrack(req, _.perform(Position.ZERO), TrackPerformance())
  }

  def readTrack[A](req: Request[Query], f: Track => A, onNotFound: => A): Try[A] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.selectedTrack)
    } yield f(track)

    Responses.returning[A](res, Some(onNotFound))
  }

}
