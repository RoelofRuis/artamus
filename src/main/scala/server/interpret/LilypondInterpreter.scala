package server.interpret

import music.collection.Track
import music.spelling.TrackSpelling
import server.interpret.lilypond.{ChordNames, LyFile, Staff}

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  import TrackSpelling._

  def interpret(track: Track): LyFile = {
    val keyTuple = for {
      key <- track.keyAtZero
    } yield (key.symbol.root, key.symbol.scale)

    LyFile(
      Staff(
        keyTuple,
        track.timeSignatureAtZero,
        track.spelledNotes,
      ),
      ChordNames(track.spelledChords),
      lyVersion,
      paperSize,
    )
  }

}
