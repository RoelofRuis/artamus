package server.domain.track

import music.{Key, Note, TimeSignature}
import server.domain.track.Track.TrackSymbol

object TrackSymbols {

  implicit object TimeSignatureSymbol extends TrackSymbol[TimeSignature]
  implicit object KeySymbol extends TrackSymbol[Key]

  implicit object NoteSymbol extends TrackSymbol[Note]

}
