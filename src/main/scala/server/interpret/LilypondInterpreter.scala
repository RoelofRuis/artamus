package server.interpret

import music.collection.Track
import music.symbols.Chord
import server.interpret.lilypond.{ChordNames, LyFile, Staff, StaffIterator}

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: Track): LyFile = {
    LyFile(
      Staff(
        new StaffIterator(track).stream
      ),
      ChordNames(track.getSymbolTrack[Chord].readAll),
      lyVersion,
      paperSize,
    )
  }

}
