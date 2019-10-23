package server.interpret.lilypond

final case class Staff(elements: Seq[String]) {

  def asString: String = {
    val staffContents = elements.mkString("\n")

    s"""\\new Staff {
       |\\numericTimeSignature
       |$staffContents
       |}""".stripMargin
  }
}
