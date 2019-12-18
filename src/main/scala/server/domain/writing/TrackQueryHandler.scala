package server.domain.writing

import javax.inject.Inject
import music.model.write.track.Track
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request
import storage.api.NotFound

import scala.util.{Failure, Success, Try}

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query]
) {

  import server.model.Tracks._
  import server.model.Workspaces._

  dispatcher.subscribe[ReadNotes.type]{ req =>
    readTrack(req, _.notes.readGroups.flatMap(_.notes).toSeq, Seq())
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    readTrack(req, _.chords.chords.values.toSeq, Seq())
  }

  dispatcher.subscribe[Perform.type]{ req =>
    import music.model.perform._

    readTrack(req, _.perform(Position.ZERO), TrackPerformance())
  }

  def readTrack[A](req: Request[Query], f: Track => A, onNotFound: => A): Try[A] = {
    val res = for {
      workspace <- req.db.getWorkspaceByOwner(req.user)
      track <- req.db.getTrackById(workspace.editedTrack)
    } yield f(track)

    res match {
      case Right(a) => Success(a)
      case Left(_: NotFound) => Success(onNotFound)
      case Left(ex) => Failure(ex)
    }
  }

}
