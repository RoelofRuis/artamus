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

  // TODO: try to rewrite as for-comprehension (and move to service?)

  dispatcher.subscribe[ReadNotes.type]{ req =>
    val res = for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.getById(workspace.editedTrack)
    } yield track.read[Note]().toSeq

    res.recover {
      case _: EntityNotFoundException => Seq()
    }
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    val res = for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.getById(workspace.editedTrack)
    } yield track.read[Chord]().toSeq

    res.recover {
      case _: EntityNotFoundException => Seq()
    }
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    val res = for {
      workspace <- workspaceRepo.getByOwner(req.user)
      track <- trackRepo.getById(workspace.editedTrack)
    } yield track.iterate(Position.ZERO).toSeq

    res.recover {
      case _: EntityNotFoundException => Seq()
    }
  }

}
