package server.model

import music.{Duration, MidiPitch}
import server.model.Track.TrackSymbol

object TrackSymbols {

  implicit object NoteProperty extends TrackSymbol[(Duration, MidiPitch)] // TODO: Move 'Note' concept to music

}
