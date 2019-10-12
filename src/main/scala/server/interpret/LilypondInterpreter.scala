package server.interpret

import music.spelling.TrackSpelling
import server.interpret.lilypond.{ChordNames, LyFile, Staff}

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: TrackSpelling): LyFile = {
    LyFile(
      Staff(
        track.spelledKey,
        track.spelledTimeSignature,
        track.spelledNotes,
      ),
      ChordNames(track.spelledChords),
      lyVersion,
      paperSize,
    )
  }

}
