package music.write

import music.symbolic._
import music.symbolic.const.Scales

trait LilypondFormat[A] {
  def toLilypond(a: A): String
}

object LilypondFormat {

  def apply[A](implicit formatter: LilypondFormat[A]): LilypondFormat[A] = formatter

  implicit class LilypondFormatOps[A : LilypondFormat](a: A) {
    def toLilypond: String = LilypondFormat[A].toLilypond(a)
  }

  implicit val noteToLilypond: LilypondFormat[Note[ScientificPitch]] = (note: Note[ScientificPitch]) => {
    Seq(
      note.pitch.musicVector.toLilypond,
      note.pitch.octave.toLilypond,
      note.duration.toLilypond
    ).mkString("")
  }

  implicit val musicVectorToLilypond: LilypondFormat[MusicVector] = (mvec: MusicVector) => {
    def accidentalText(a: Accidental, acc: String = "", suppressE: Boolean = false): String = {
      a match {
        case Accidental(0) => acc
        case Accidental(v) if v > 0 => accidentalText(Accidental(v - 1), acc + "is")
        case Accidental(v) if v < 0 => accidentalText(Accidental(v + 1), acc + (if (suppressE) "s" else "es"))
      }
    }
    val stepValue = mvec.step.value
    mvec.step.toLilypond + accidentalText(mvec.acc, suppressE = stepValue == 2 || stepValue == 5)
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
    s"\\time ${timeSignature.num}/${timeSignature.denom}"
  }

  implicit val keyToLilypond: LilypondFormat[Key] = (key: Key) => {
    // TODO: implement http://lilypond.org/doc/v2.18/Documentation/notation/displaying-pitches#key-signature
    val pitch = key.root.toLilypond
    val mode = key.scale match {
      case Scales.MAJOR => "\\major"
      case Scales.MINOR => "\\minor"
      case _ => "\\major"
    }
    s"\\key $pitch $mode"
  }

}