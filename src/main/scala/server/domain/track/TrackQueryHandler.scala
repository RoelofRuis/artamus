package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.domain.track.symbol.{Chord, Note}
import music.domain.workspace.WorkspaceRepository
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request

import scala.util.{Failure, Success}

private[server] class TrackQueryHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  trackRepo: TrackRepository,
  dispatcher: Dispatcher[Request, Query]
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    workspaceRepo.getByOwner(req.user) match {
      case Failure(ex) => Failure(ex)
      case Success(workspace) if workspace.editedTrack.isDefined =>
        trackRepo.getById(workspace.editedTrack.get) match {
          case Some(Failure(ex)) => Failure(ex)
          case Some(Success(track)) => Success(track.read[Note]().toSeq)
          case None => Success(Seq())
        }
      case _ => Success(Seq())
    }
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    workspaceRepo.getByOwner(req.user) match {
      case Failure(ex) => Failure(ex)
      case Success(workspace) if workspace.editedTrack.isDefined =>
        trackRepo.getById(workspace.editedTrack.get) match {
          case Some(Failure(ex)) => Failure(ex)
          case Some(Success(track)) => Success(track.read[Chord]().toSeq)
          case None => Success(Seq())
        }
      case _ => Success(Seq())
    }
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    workspaceRepo.getByOwner(req.user) match {
      case Failure(ex) => Failure(ex)
      case Success(workspace) if workspace.editedTrack.isDefined =>
        trackRepo.getById(workspace.editedTrack.get) match {
          case Some(Failure(ex)) => Failure(ex)
          case Some(Success(track)) => Success(track.iterate(Position.ZERO).toSeq)
          case None => Success(Seq())
        }
      case _ => Success(Seq())
    }
  }

}
