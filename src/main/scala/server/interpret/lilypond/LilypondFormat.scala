package server.interpret.lilypond

import music.analysis.TwelveToneTuning.TwelveToneFunctions
import music.primitives._
import music.symbol.{Key, TimeSignature}

import scala.annotation.tailrec
import scala.collection.SortedSet

trait LilypondFormat[A] {
  def toLilypond(a: A): String
}

object LilypondFormat {

  def apply[A](implicit formatter: LilypondFormat[A]): LilypondFormat[A] = formatter

  implicit class LilypondFormatOps[A : LilypondFormat](a: A) {
    def toLilypond: String = LilypondFormat[A].toLilypond(a)
  }

  def restToLilypond(duration: WriteableDuration, silent: Boolean): String = {
    if (silent) s"s${duration.toLilypond}"
    else s"r${duration.toLilypond}"
  }

  implicit val spelledChordToLilypond: LilypondFormat[WriteableChord] = chord => {
    val spelledRoot = chord.root.toLilypond
    val spelledDur = chord.duration.toLilypond
    spelledRoot + spelledDur + chord.functions.toLilypond
  }

  implicit val chordFunctionsToLilypond: LilypondFormat[SortedSet[Function]] = { functions =>
    if (functions.contains(TwelveToneFunctions.FLAT_THREE)) ":m"
    else ""
  }

  implicit val simultaneousPitchesToLilypond: LilypondFormat[WriteableNoteGroup] = noteGroup => {
      if (noteGroup.isEmpty) ""
      else {
        val tie = (if (noteGroup.tieToNext) "~" else "")
        if (noteGroup.isChord) {
          val lilyNotes = noteGroup.notes
            .map { pitch => pitch.spelling.toLilypond + pitch.octave.toLilypond + tie }
            .mkString("<", " ", ">")

          lilyNotes + noteGroup.duration.toLilypond
        } else {
          val pitchSpelling = noteGroup.notes.head.spelling.toLilypond
          val octaveSpelling = noteGroup.notes.head.octave.toLilypond
          val durationSpelling = noteGroup.duration.toLilypond
          pitchSpelling + octaveSpelling + durationSpelling + tie
        }
      }
    }

  implicit val spelledPitchToLilypond: LilypondFormat[PitchSpelling] = spelling => {
    @tailrec
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
      case _ => ""
    }
  }

  implicit val octaveToLilypond: LilypondFormat[Octave] = octave => {
    // 3th midi octave is unaltered in lilypond notation
    octave.value - 3 match {
      case i if i == 0 => ""
      case i if i < 0 => "," * i
      case i if i > 0 => "'" * i
    }
  }

  implicit val writableDurationToLilypond: LilypondFormat[WriteableDuration] = dur => {
    s"${dur.base.d}" + ("." * dur.dots)
  }

  implicit val timeSignatureToLilypond: LilypondFormat[TimeSignature] = timeSignature => {
    val division = timeSignature.division
    s"\\time ${division.num}/${division.denom}"
  }

  implicit val keyToLilypond: LilypondFormat[Key] = key => {
    // TODO: implement http://lilypond.org/doc/v2.18/Documentation/notation/displaying-pitches#key-signature
    val mode = key.scale match {
      case Scale.MAJOR => "\\major"
      case Scale.MINOR => "\\minor"
      case _ => "\\major"
    }
    val pitch = key.root.toLilypond
    s"\\key $pitch $mode"
  }

}
