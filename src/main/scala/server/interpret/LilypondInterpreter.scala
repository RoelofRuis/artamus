package server.interpret

import music.collection.Track
import music.primitives.Scale
import music.spelling.{SpelledPitch, TrackSpelling}
import server.interpret.lilypond.{ChordNames, LyFile, Staff}

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  import TrackSpelling._

  def interpret(track: Track): LyFile = {
    val keyTuple = for {
      key <- track.spelledKey
      root <- key.get[SpelledPitch]
      scale <- key.get[Scale]
    } yield (root, scale)

    LyFile(
      Staff(
        keyTuple,
        track.spelledTimeSignature,
        track.spelledNotes,
      ),
      ChordNames(track.spelledChords),
      lyVersion,
      paperSize,
    )
  }

}
