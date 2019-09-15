package music.write

final case class LilypondFile(
  noteInput: String,
  timeSignature: Option[String],
  key: Option[String],
) {

  def getStringContents: String = {
    val content = Seq(timeSignature, key, Some(noteInput)).collect { case Some(input) => input }.mkString("\n")

    s"""\\version "2.18"
      |
      |{
      |$content
      |}
      |""".stripMargin
  }
}
