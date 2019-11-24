package server.interpret

import music.domain.track.Track
import server.rendering.LyFile

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
