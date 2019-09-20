package server.domain.track

import javax.inject.Inject
import music.symbolic.Note
import music.symbolic.Pitched.PitchClass
import music.symbolic.tuning.TwelveToneEqualTemprament
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  val tuning: TwelveToneEqualTemprament.type = TwelveToneEqualTemprament

  import music.symbolic.properties.Symbols._

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.getTrack.getAllStackedSymbols[Note[PitchClass]].map {
      case (_, notes) => notes.map(note => tuning.pitchToNoteNumber(note.pitch).value).toList
    }.toList
  }

}
