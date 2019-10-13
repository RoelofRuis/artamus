package server.interpret.lilypond

import music.primitives._
import music.spelling.{SpelledNote, SpelledPitch}

final case class Staff(
  key: Option[(SpelledPitch, Scale)],
  timeSignature: Option[TimeSignatureDivision],
  notes: Seq[Seq[SpelledNote]]
) {

  import server.interpret.lilypond.LilypondFormat._

  def asString: String = {
    val staffContents = Seq(
      timeSignature.map(_.toLilypond),
      key.map(_.toLilypond),
      Some(notes.map(_.toLilypond).mkString(" "))
    ).collect { case Some(input) => input }.mkString("\n")

    s"""\\new Staff {
       |\\numericTimeSignature
       |$staffContents
       |}""".stripMargin
  }
}
