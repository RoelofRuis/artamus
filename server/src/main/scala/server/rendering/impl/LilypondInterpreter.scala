package server.rendering.impl

import nl.roelofruis.artamus.core.model.display.Display
import server.rendering.model.LilypondFormat

private[rendering] class LilypondInterpreter(
  lyVersion: String,
  paperSize: String
) {

  import LilypondFormat._

  def interpret(track: Display): LyFile = {
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
