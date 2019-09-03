package server.model

import music.{Key, TimeSignature}
import server.model.Track.TrackProperty

object TrackProperties {

  implicit object TimeSignatureProperty extends TrackProperty[TimeSignature]
  implicit object KeyProperty extends TrackProperty[Key]

}
