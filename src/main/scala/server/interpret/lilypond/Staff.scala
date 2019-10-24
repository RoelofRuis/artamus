package server.interpret.lilypond

final case class Staff(elements: Seq[String]) extends Container {
  def asString: String =
    s"""\\new Staff {
       |\\numericTimeSignature
       |${elements.mkString("\n")}
       |}""".stripMargin
}
