package server.interpret.lilypond

import music.primitives._
import music.spelling.{SpelledChord, SpelledNote}
import music.symbols.{Key, TimeSignature}

// TODO: distribute over classes representing the lilypond structure!
trait LilypondFormat[A] {
  def toLilypond(a: A): String
}

object LilypondFormat {

  def apply[A](implicit formatter: LilypondFormat[A]): LilypondFormat[A] = formatter

  implicit class LilypondFormatOps[A : LilypondFormat](a: A) {
    def toLilypond: String = LilypondFormat[A].toLilypond(a)
  }

  implicit val spelledChordToLilypond: LilypondFormat[SpelledChord] = (chord: SpelledChord) => {
    chord.root.toLilypond + chord.duration.toLilypond
  }

  implicit val simultaneousNotesToLilypond: LilypondFormat[Seq[SpelledNote]] = (notes: Seq[SpelledNote]) => {
    val dur = notes.map(_.duration).max
    // TODO: no brackets for single notes!
    notes.map { note =>
      Seq(
        note.pitch.toLilypond,
        note.octave.toLilypond,
      ).mkString("")
    }.mkString("<", " ", ">") + dur.toLilypond
  }

  implicit val spelledPitchToLilypond: LilypondFormat[PitchSpelling] = (spelling: PitchSpelling) => {
    def accidentalText(a: Accidental, acc: String = "", suppressE: Boolean = false): String = {
      a match {
        case Accidental(0) => acc
        case Accidental(v) if v > 0 => accidentalText(Accidental(v - 1), acc + "is")
        case Accidental(v) if v < 0 => accidentalText(Accidental(v + 1), acc + (if (suppressE) "s" else "es"))
      }
    }
    val stepValue = spelling.step.value
    spelling.step.toLilypond + accidentalText(spelling.accidental, suppressE = stepValue == 2 || stepValue == 5)
  }

  implicit val stepToLilypond: LilypondFormat[Step] = (step: Step) => {
    step.value match {
      case 0 => "c"
      case 1 => "d"
      case 2 => "e"
      case 3 => "f"
      case 4 => "g"
      case 5 => "a"
      case 6 => "b"
    }
  }

  implicit val octaveToLilypond: LilypondFormat[Octave] = (octave: Octave) => {
    // 3th midi octave is unaltered in lilypond notation
    octave.value - 3 match {
      case i if i == 0 => ""
      case i if i < 0 => "," * i
      case i if i > 0 => "'" * i
    }
  }

  implicit val durationToLilypond: LilypondFormat[Duration] = (duration: Duration) => {
    s"${duration.value.d}"
  }

  implicit val timeSignatureToLilypond: LilypondFormat[TimeSignature] = (timeSignature: TimeSignature) => {
    val division = timeSignature.division
    s"\\time ${division.num}/${division.denom}"
  }

  implicit val keyToLilypond: LilypondFormat[Key] = key => {
    // TODO: implement http://lilypond.org/doc/v2.18/Documentation/notation/displaying-pitches#key-signature
    val pitch = key.root.toLilypond
    val mode = key.scale match {
      case Scale.MAJOR => "\\major"
      case Scale.MINOR => "\\minor"
      case _ => "\\major"
    }
    s"\\key $pitch $mode"
  }

}
