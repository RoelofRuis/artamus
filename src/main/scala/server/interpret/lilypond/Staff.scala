package server.interpret.lilypond

import music.spelling.SpelledNote
import music.symbols.{Key, TimeSignature}

final case class Staff(
  key: Option[Key],
  timeSignature: Option[TimeSignature],
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
