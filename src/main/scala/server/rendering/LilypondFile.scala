package server.rendering

import music.symbolic.pitch.SpelledPitch
import music.symbolic.symbol.{Key, Note, TimeSignature}

final case class LilypondFile(
  notes: Seq[Seq[Note[SpelledPitch]]],
  timeSignature: Option[TimeSignature],
  key: Option[Key],
) {

  import server.rendering.LilypondFormat._

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
