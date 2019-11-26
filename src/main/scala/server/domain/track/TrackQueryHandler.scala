package server.domain.track

import javax.inject.Inject
import music.domain.track.symbol.{Chord, Note}
import music.domain.workspace.WorkspaceRepository
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request

import scala.util.Success

private[server] class TrackQueryHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  dispatcher: Dispatcher[Request, Query]
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    Success(
      workspaceRepo
        .getByOwner(req.user)
        .editedTrack
        .read[Note]()
        .toSeq
    )
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    Success(
      workspaceRepo
        .getByOwner(req.user)
        .editedTrack
        .read[Chord]()
        .toSeq
    )
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    Success(
      workspaceRepo
        .getByOwner(req.user)
        .editedTrack
        .iterate(Position.ZERO)
        .toSeq
    )
  }

}
