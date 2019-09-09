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
    val accidentalText = mvec.acc match {
      case Accidental(0) => ""
      case Accidental(1) => "is"
      case Accidental(-1) => if (stepValue == 2 || stepValue == 4) "s" else "es"
      case _ => "" // TODO: support the recursive case!
    }
    name + accidentalText
  }

  def durationToLily(duration: Duration): String = s"${duration.value.d}"

  val notes: List[(Duration, MusicVector)] = List(
    (Duration.QUARTER, MusicVector(Step(0), Accidental(0))),
    (Duration.QUARTER, MusicVector(Step(2), Accidental(-1))),
    (Duration.QUARTER, MusicVector(Step(3), Accidental(0))),
    (Duration.QUARTER, MusicVector(Step(3), Accidental(1)))
  )

  println(notesToLily(notes))

}
