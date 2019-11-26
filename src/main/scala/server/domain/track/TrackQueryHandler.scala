package server.domain.track

import javax.inject.Inject
import music.domain.track.symbol.{Chord, Note}
import music.domain.workspace.WorkspaceRepository
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request

private[server] class TrackQueryHandler @Inject() (
  workspaceRepo: WorkspaceRepository,
  dispatcher: Dispatcher[Request, Query]
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
    } yield workspace.editedTrack.read[Note]().toSeq
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    for {
      workspace <- workspaceRepo.getByOwner(req.user)
    } yield workspace.editedTrack.read[Chord]().toSeq
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    for {
      workspace <- workspaceRepo.getByOwner(req.user)
    } yield workspace.editedTrack.iterate(Position.ZERO).toSeq
  }

}
