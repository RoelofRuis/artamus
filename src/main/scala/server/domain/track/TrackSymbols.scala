package server.domain.track

import music.Note
import server.domain.track.Track.TrackSymbol

object TrackSymbols {

  implicit object NoteProperty extends TrackSymbol[Note] // TODO: Move 'Note' concept to music

}
