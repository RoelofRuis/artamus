package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.domain.track.symbol.{Chord, Note}
import music.domain.workspace.WorkspaceRepository
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request
import server.storage.EntityNotFoundException

import scala.util.{Failure, Success}

private[server] class TrackQueryHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  trackRepo: TrackRepository,
  dispatcher: Dispatcher[Request, Query]
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    workspaceRepo.getByOwner(req.user) match {
      case Success(workspace) =>
        trackRepo.getById(workspace.editedTrack) match {
          case Success(track) => Success(track.read[Note]().toSeq)
          case Failure(_: EntityNotFoundException) => Success(Seq())
          case Failure(ex) => Failure(ex)
        }
      case Failure(_: EntityNotFoundException) => Success(Seq())
      case Failure(ex) => Failure(ex)
    }
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    workspaceRepo.getByOwner(req.user) match {
      case Success(workspace) =>
        trackRepo.getById(workspace.editedTrack) match {
          case Success(track) => Success(track.read[Chord]().toSeq)
          case Failure(ex) => Failure(ex)
          case Failure(_: EntityNotFoundException) => Success(Seq())
        }
      case Failure(_: EntityNotFoundException) => Success(Seq())
      case Failure(ex) => Failure(ex)
    }
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    workspaceRepo.getByOwner(req.user) match {
      case Success(workspace) =>
        trackRepo.getById(workspace.editedTrack) match {
          case Failure(ex) => Failure(ex)
          case Success(track) => Success(track.iterate(Position.ZERO).toSeq)
          case Failure(_: EntityNotFoundException) => Success(Seq())
        }
      case Failure(_: EntityNotFoundException) => Success(Seq())
      case Failure(ex) => Failure(ex)
    }
  }

}
