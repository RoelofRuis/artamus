package music.write

import music.{Key, Note, ScientificPitch, TimeSignature}

final case class LilypondFile(
  notes: Iterable[Note[ScientificPitch]],
  timeSignature: Option[TimeSignature],
  key: Option[Key],
) {

  import music.write.LilypondFormat._

  def getStringContents: String = {
    val content = Seq(
      timeSignature.map(_.toLilypond),
      key.map(_.toLilypond),
      Some(notes.map(_.toLilypond).mkString(" "))
    ).collect { case Some(input) => input }.mkString("\n")

    s"""\\version "2.18"
      |
      |{
      |$content
      |}
      |""".stripMargin
  }
}
