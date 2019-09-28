package server.rendering

import music.symbolic.pitched.Spelled
import music.symbolic.{Key, Note, TimeSignature}

final case class LilypondFile(
  notes: Seq[Seq[Note[Spelled]]],
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
      |\\header {
      |  tagline = ##f
      |}
      |
      |\\layout{
      |  clip-regions
      |  = #(list
      |      (cons
      |       (make-rhythmic-location 0 0 0)
      |       (make-rhythmic-location 1 0 0)))
      |}
      |
      |{
      |\\numericTimeSignature
      |$content
      |}
      |""".stripMargin
  }
}
