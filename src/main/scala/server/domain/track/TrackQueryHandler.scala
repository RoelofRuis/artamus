package server.domain.track

import javax.inject.Inject
import music.{MidiPitch, Note}
import protocol.{Dispatcher, Query}

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.properties.Symbols._
  import music.properties.Pitch._

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.getTrack.getSymbols[Note[MidiPitch]]
      .map(_.pitch.getMidiNoteNumber.value)
      .toList
  }

}
