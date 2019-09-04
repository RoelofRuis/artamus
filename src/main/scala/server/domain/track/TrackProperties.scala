package server.domain.track

import music.{Key, TimeSignature}
import server.domain.track.Track.TrackProperty

object TrackProperties {

  implicit object TimeSignatureProperty extends TrackProperty[TimeSignature]
  implicit object KeyProperty extends TrackProperty[Key]

}
