package server.interpret

import music.domain.display.DisplayTrack
import server.rendering.LyFile

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: DisplayTrack): LyFile = {
    LyFile(LilypondContexts.file(
      Seq(LilypondContexts.staff(track), LilypondContexts.chords(track)).mkString("\n"),
      lyVersion,
      paperSize,
    ))
  }

}
