package server.interpret

import music.collection.Track
import music.spelling.TrackSpelling
import server.interpret.lilypond.{ChordNames, LyFile, Staff, StaffIterator}

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  import TrackSpelling._

  def interpret(track: Track): LyFile = {
    LyFile(
      Staff(
        new StaffIterator(track).stream
      ),
      ChordNames(track.spelledChords),
      lyVersion,
      paperSize,
    )
  }

}
