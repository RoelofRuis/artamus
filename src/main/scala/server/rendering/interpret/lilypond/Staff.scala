package server.rendering.interpret.lilypond

import music.primitives._

final case class Staff(
  key: Option[Key],
  timeSignature: Option[TimeSignature],
  notes: Seq[Seq[SpelledNote]]
) {

  import server.rendering.interpret.lilypond.LilypondFormat._

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
