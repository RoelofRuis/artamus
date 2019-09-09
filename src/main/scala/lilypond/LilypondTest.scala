package lilypond

import music._

object LilypondTest extends App {

  def notesToLily(notes: List[(Duration, MusicVector)]): String = {
    notes.map { case (duration, mvec) =>
      musicVectorToLily(mvec) + durationToLily(duration)
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
    name + accidentalText(mvec.acc, suppressE = true)
  }

  def durationToLily(duration: Duration): String = s"${duration.value.d}"

  val notes: List[(Duration, MusicVector)] = List(
    (Duration.QUARTER, MusicVector(Step(0), Accidental(0))),
    (Duration.QUARTER, MusicVector(Step(2), Accidental(-1))),
    (Duration.QUARTER, MusicVector(Step(3), Accidental(0))),
    (Duration.QUARTER, MusicVector(Step(3), Accidental(1))),
    (Duration.QUARTER, MusicVector(Step(3), Accidental(2))),
  )

  println(notesToLily(notes))

}
