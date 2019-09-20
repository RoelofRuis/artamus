package music.write

import music.symbolic.Pitched.Spelled
import music.symbolic._

final case class LilypondFile(
  notes: Seq[Seq[Note[Spelled]]],
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
      |\\numericTimeSignature
      |$content
      |}
      |""".stripMargin
  }
}
