package server.domain.track

import javax.inject.Inject
import music.{MidiPitch, Note, Position}
import protocol.Query
import pubsub.Dispatcher

private[server] class TrackQueryHandler @Inject() (
  dispatcher: Dispatcher[Query],
  state: TrackState
) {

  import music.properties.Pitch.midiPitchHasExactPitch
  import music.properties.Symbols._

  dispatcher.subscribe[GetMidiPitches.type]{ _ =>
    state.getTrack.getStackedSymbolsAt[Note[MidiPitch]](Position.zero)
      .map(note => midiPitchHasExactPitch.getMidiNoteNumber(note.pitch).value)
      .toList
  }

}
