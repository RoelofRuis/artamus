package server.rendering.interpret

import music.symbolic.pitch.SpelledNote
import music.symbolic.symbol.{Key, TimeSignature}

final case class LilypondFile(
  notes: Seq[Seq[SpelledNote]],
  timeSignature: Option[TimeSignature],
  key: Option[Key],
) {

  import server.rendering.interpret.LilypondFormat._

  def getStringContents: String = {
    val content = Seq(
      timeSignature.map(_.toLilypond),
      key.map(_.toLilypond),
      Some(notes.map(_.toLilypond).mkString(" "))
    ).collect { case Some(input) => input }.mkString("\n")

    s"""\\version "2.18"
      |
      |\\paper {
      |  #(set-paper-size "a6landscape")
      |}
      |
      |\\header {
      |  tagline = ##f
      |}
      |
      |{
      |\\numericTimeSignature
      |$content
      |}
      |""".stripMargin
  }
}
