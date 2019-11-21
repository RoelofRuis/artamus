package server.domain.track

import javax.inject.Inject
import music.domain.track.TrackRepository
import music.domain.track.symbol.{Chord, Note}
import music.math.temporal.Position
import protocol.Query
import pubsub.Dispatcher
import server.Request

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Request, Query],
  trackRepository: TrackRepository,
) {

  dispatcher.subscribe[ReadNotes.type]{ req =>
    val trackId = req.user.workspace.editedTrack

    trackRepository
      .getById(trackId)
        .map(_.read[Note]().toSeq)
        .getOrElse(Seq())
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    val trackId = req.user.workspace.editedTrack

    trackRepository
      .getById(trackId)
      .map(_.read[Chord]().toSeq)
      .getOrElse(Seq())
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    val trackId = req.user.workspace.editedTrack

    trackRepository
      .getById(trackId)
      .map(_.iterate(Position.ZERO).toSeq)
      .getOrElse(Seq())
  }

}
