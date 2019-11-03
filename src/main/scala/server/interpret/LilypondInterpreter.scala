package server.interpret

import music.symbol.collection.Track
import server.interpret.lilypond._
import server.rendering.service.LilypondCommandLineExecutor.LyFile

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: Track): LyFile = {
    LyFile(LilypondContexts.file(
      Seq(LilypondContexts.staff(track), LilypondContexts.chords(track)).mkString("\n"),
      lyVersion,
      paperSize,
    ))
  }

}
