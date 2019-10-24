package server.interpret.lilypond

final case class ChordNames(
  chords: Seq[String]
) {

  def asString: String = {
    s"""\\new ChordNames {
       |\\chordmode {
       |${chords.mkString(" ")}
       |}
       |}""".stripMargin
  }
}
