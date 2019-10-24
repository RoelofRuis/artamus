package server.interpret

import music.collection.Track
import server.interpret.lilypond._

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: Track): LyFile = {
    LyFile(
      Staff(
        new StaffIterator(track).stream
      ),
      ChordNames(
        new ChordIterator(track).stream
      ),
      lyVersion,
      paperSize,
    )
  }

}
