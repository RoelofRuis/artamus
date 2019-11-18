package server.interpret

import music.domain.track.Track2
import server.rendering.LyFile

class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: Track2): LyFile = {
    LyFile(LilypondContexts.file(
      Seq(LilypondContexts.staff(track), LilypondContexts.chords(track)).mkString("\n"),
      lyVersion,
      paperSize,
    ))
  }

}
