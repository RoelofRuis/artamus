package music.write

import music._

// TODO: improve this once Transformation to ScientificPitch is further completed
object LilypondFormatDummy {

  def notesToLilypond(notes: Iterable[Note[ScientificPitch]]): String = {
    notes.map { note =>
      musicVectorToLily(note.pitch.musicVector) + durationToLily(note.duration)
    }.mkString(" ")
  }

  def musicVectorToLily(mvec: MusicVector): String = {
    val stepValue = mvec.step.value
    val name = stepValue match {
      case 0 => "c"
      case 1 => "d"
      case 2 => "e"
      case 3 => "f"
      case 4 => "g"
      case 5 => "a"
      case 6 => "b"
    }
    def accidentalText(a: Accidental, acc: String = "", suppressE: Boolean = false): String = {
      a match {
        case Accidental(0) => acc
        case Accidental(v) if v > 0 => accidentalText(Accidental(v - 1), acc + "is")
        case Accidental(v) if v < 0 => accidentalText(Accidental(v + 1), acc + (if (suppressE) "s" else "es"))
      }
    }
    name + accidentalText(mvec.acc, suppressE = stepValue == 2 || stepValue == 5)
  }

  def durationToLily(duration: Duration): String = s"${duration.value.d}"

  def compileFile(noteInput: String): String =
    s"""\\version "2.18"
      |
      |{
      |  $noteInput
      |}
      |""".stripMargin

}
