package server.interpret

import music.symbol.collection.Track
import server.interpret.lilypond._

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: Track): LyFile = {
    LyFile(
      Seq(
        Staff(new StaffIterator(track).stream),
        ChordNames(new ChordIterator(track).stream)
      ),
      lyVersion,
      paperSize,
    )
  }

}
