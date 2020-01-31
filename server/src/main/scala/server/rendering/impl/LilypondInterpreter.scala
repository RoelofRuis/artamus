package server.rendering.impl

import music.model.display.TrackDisplay
import server.rendering.model.LilypondFormat

private[rendering] class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  import LilypondFormat._

  def interpret(track: TrackDisplay): LyFile = {
    val contents = s"""|\\version "$lyVersion"
        |
        |\\paper {
        |  #(set-paper-size "$paperSize")
        |}
        |
        |\\header {
        |  tagline = ##f
        |}
        |
        |\\score {
        |${track.staffGroup.toLilypond}
        |}
        |""".stripMargin
    LyFile(contents)
  }

}
