package server.domain.track

import javax.inject.Inject
import music.domain.track.{Track, TrackRepository}
import music.domain.user.User
import music.domain.workspace.WorkspaceRepository
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request
import server.storage.EntityNotFoundException

import scala.util.Try

private[server] class TrackQueryHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  trackRepo: TrackRepository,
  dispatcher: Dispatcher[Request, Query]
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    readTrack(req.user, _.notes.readGroups.flatMap(_.notes).toSeq, Seq())
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    readTrack(req.user, _.chords.chords.values.toSeq, Seq())
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    readTrack(req.user, _.iterate(Position.ZERO).toSeq, Seq())
  }

  def readTrack[A](user: User, f: Track => A, onNotFound: => A): Try[A] = {
    val res = for {
      workspace <- workspaceRepo.getByOwner(user)
      track <- trackRepo.getById(workspace.editedTrack)
    } yield f(track)

    res.recover {
      case _: EntityNotFoundException => onNotFound
    }
  }

}
