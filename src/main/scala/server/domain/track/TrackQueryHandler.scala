package server.domain.track

import javax.inject.Inject
import music.{Duration, MidiPitch, Note, Position}
import protocol.{Dispatcher, Query}

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.properties.Symbols._
  import music.properties.Pitch.midiPitchHasExactPitch

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.getTrack.getStackedSymbolsAt[Note[MidiPitch]](Position(Duration.QUARTER, 0))
      .map(note => midiPitchHasExactPitch.getMidiNoteNumber(note.pitch).value)
      .toList
  }

}
