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
    req
      .user
      .workspace
      .editedTrack
      .map(_.read[Note]().toSeq)
      .getOrElse(Seq())
  }

  dispatcher.subscribe[ReadChords.type]{ req =>
    req
      .user
      .workspace
      .editedTrack
      .map(_.read[Chord]().toSeq)
      .getOrElse(Seq())
  }

  dispatcher.subscribe[ReadMidiNotes.type]{ req =>
    import music.playback._

    req
      .user
      .workspace
      .editedTrack
      .map(_.iterate(Position.ZERO).toSeq)
      .getOrElse(Seq())
  }

}
