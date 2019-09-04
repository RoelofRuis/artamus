package server.domain.track

import music.{Duration, MidiPitch}
import server.domain.track.Track.TrackSymbol

object TrackSymbols {

  implicit object NoteProperty extends TrackSymbol[(Duration, MidiPitch)] // TODO: Move 'Note' concept to music

}
