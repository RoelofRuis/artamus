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
    LyFile(
      Staff(
        track.keyAtZero,
        track.timeSignatureAtZero,
        track.spelledNotes,
      ),
      ChordNames(track.spelledChords),
      lyVersion,
      paperSize,
    )
  }

}
