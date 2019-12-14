package server.domain.track

import javax.inject.Inject
import music.domain.track.Track
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request
import server.storage.entity.NotFound

import scala.util.{Failure, Success, Try}

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query]
) {

  import server.storage.entity.Tracks._
  import server.storage.entity.Workspaces._

  dispatcher.subscribe[ReadNotes.type]{ req =>
    readTrack(req, _.notes.readGroups.flatMap(_.notes).toSeq, Seq())
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    readTrack(req, _.chords.chords.values.toSeq, Seq())
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    readTrack(req, _.iterate(Position.ZERO).toSeq, Seq())
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
