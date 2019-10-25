package server.interpret.lilypond

import music.primitives._
import music.symbol.{Chord, Key, Note, TimeSignature}

import scala.annotation.tailrec

trait LilypondFormat[A] {
  def toLilypond(a: A): Option[String]
}

object LilypondFormat {

  def apply[A](implicit formatter: LilypondFormat[A]): LilypondFormat[A] = formatter

  implicit class LilypondFormatOps[A : LilypondFormat](a: A) {
    def toLilypond: Option[String] = LilypondFormat[A].toLilypond(a)
  }

  def restToLilypond(duration: Duration, silent: Boolean): String = {
    if (silent) s"s${duration.toLilypond.get}"
    else s"r${duration.toLilypond.get}"
  }

  implicit val spelledChordToLilypond: LilypondFormat[Chord] = (chord: Chord) => {
    for {
      root <- chord.rootSpelling
      dur <- chord.duration
      spelledRoot <- root.toLilypond
      spelledDur <- dur.toLilypond
    } yield spelledRoot + spelledDur
  }

  implicit val simultaneousNotesToLilypond: LilypondFormat[Seq[Note]] = (notes: Seq[Note]) => {
    if (notes.isEmpty) None
    else {
      val noteString = notes.flatMap { note =>
        for {
          scientificPitch <- note.scientificPitch
          pitchSpelling <- scientificPitch.spelling.toLilypond
          octaveSpelling <- scientificPitch.octave.toLilypond
        } yield pitchSpelling + octaveSpelling
      }
      val noteColl = if (notes.size == 1) noteString.mkString("") else noteString.mkString("<", " ", ">")
      for {
        dur <- notes.map(_.duration).max.toLilypond
      } yield noteColl + dur
    }
  }

  implicit val spelledPitchToLilypond: LilypondFormat[PitchSpelling] = (spelling: PitchSpelling) => {
    @tailrec
    def accidentalText(a: Accidental, acc: String = "", suppressE: Boolean = false): String = {
      a match {
        case Accidental(0) => acc
        case Accidental(v) if v > 0 => accidentalText(Accidental(v - 1), acc + "is")
        case Accidental(v) if v < 0 => accidentalText(Accidental(v + 1), acc + (if (suppressE) "s" else "es"))
      }
    }
    spelling.step.toLilypond match {
      case Some(spelledStep) =>
        val stepValue = spelling.step.value
        Some(spelledStep + accidentalText(spelling.accidental, suppressE = stepValue == 2 || stepValue == 5))
      case None => None
    }
  }

  implicit val stepToLilypond: LilypondFormat[Step] = (step: Step) => {
    step.value match {
      case 0 => Some("c")
      case 1 => Some("d")
      case 2 => Some("e")
      case 3 => Some("f")
      case 4 => Some("g")
      case 5 => Some("a")
      case 6 => Some("b")
      case _ => None
    }
  }

  implicit val octaveToLilypond: LilypondFormat[Octave] = octave => {
    // 3th midi octave is unaltered in lilypond notation
    octave.value - 3 match {
      case i if i == 0 => Some("")
      case i if i < 0 => Some("," * i)
      case i if i > 0 => Some("'" * i)
    }
  }

  implicit val durationToLilypond: LilypondFormat[Duration] = duration => Some(s"${duration.value.d}")

  implicit val timeSignatureToLilypond: LilypondFormat[TimeSignature] = timeSignature => {
    val division = timeSignature.division
    Some(s"\\time ${division.num}/${division.denom}")
  }

  implicit val keyToLilypond: LilypondFormat[Key] = key => {
    // TODO: implement http://lilypond.org/doc/v2.18/Documentation/notation/displaying-pitches#key-signature
    val mode = key.scale match {
      case Scale.MAJOR => "\\major"
      case Scale.MINOR => "\\minor"
      case _ => "\\major"
    }
    key.root.toLilypond match {
      case Some(pitch) => Some(s"\\key $pitch $mode")
      case None => None
    }
  }

}
