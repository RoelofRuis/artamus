package server.rendering.impl

import music.model.display.TrackDisplay
import server.rendering.model.LilypondContexts

private[rendering] class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  def interpret(track: TrackDisplay): LyFile = {
    LyFile(LilypondContexts.file(
      Seq(LilypondContexts.staff(track), LilypondContexts.chords(track)).mkString("\n"),
      lyVersion,
      paperSize,
    ))
  }

}
