package server.rendering.model

import music.analysis.TwelveToneTuning.TwelveToneFunctions
import music.model.display.chord.ChordStaff
import music.model.display.chord.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import music.model.display.staff.{Bass, Clef, Staff, Treble}
import music.model.display.staff.StaffGlyph.{FullBarRestGlyph, KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import music.primitives._

import scala.annotation.tailrec

private[rendering] trait LilypondFormat[A] {
  def toLilypond(a: A): String
}

private[rendering] object LilypondFormat {

  def apply[A](implicit formatter: LilypondFormat[A]): LilypondFormat[A] = formatter

  implicit class LilypondFormatOps[A : LilypondFormat](a: A) {
    def toLilypond: String = LilypondFormat[A].toLilypond(a)
  }

  implicit val staffFormat: LilypondFormat[Staff] = staff => {
    val contents = staff.glyphs.map {
      case g: NoteGroupGlyph => g.toLilypond
      case g: RestGlyph => g.toLilypond
      case g: FullBarRestGlyph => g.toLilypond
      case g: KeyGlyph => g.toLilypond
      case g: TimeSignatureGlyph => g.toLilypond
    }.mkString("\n")
    s"""\\new Staff {
       |\\numericTimeSignature
       |\\override Score.BarNumber.break-visibility = ##(#f #t #t)
       |\\set Score.barNumberVisibility = #all-bar-numbers-visible
       |\\bar ""
       |${staff.clef.toLilypond}
       |$contents
       |\\bar "|."
       |}
       |""".stripMargin
  }

  implicit val clefFormat: LilypondFormat[Clef] = {
    case Treble => "\\clef treble"
    case Bass => "\\clef bass"
  }

  implicit val chordStaffFormat: LilypondFormat[ChordStaff] = staff => {
    val contents = staff.glyphs.map {
      case g: ChordNameGlyph => g.toLilypond
      case g: ChordRestGlyph => g.toLilypond
    }.mkString("\n")

    s"""\\new ChordNames {
       |\\chordmode {
       |$contents
       |}
       |}""".stripMargin
  }

  implicit val fullBarRestGlyphToLilypond: LilypondFormat[FullBarRestGlyph] = rest => s"R" + rest.numberOfBars

  implicit val restToLilypond: LilypondFormat[RestGlyph] = rest => {
    val durationString = rest.duration.toLilypond
    if (rest.silent) "s" + durationString else "r" + durationString
  }

  implicit val chordRestToLilypond: LilypondFormat[ChordRestGlyph] = rest => {
    s"s${rest.duration.toLilypond}"
  }

  implicit val spelledChordToLilypond: LilypondFormat[ChordNameGlyph] = chord => {
    val spelledRoot = chord.root.toLilypond
    val spelledDur = chord.duration.toLilypond
    spelledRoot + spelledDur + chord.functions.toLilypond
  }

  implicit val chordFunctionsToLilypond: LilypondFormat[Set[Function]] = { functions =>
    if (functions.contains(TwelveToneFunctions.FLAT_THREE)) ":m"
    else ""
  }

  implicit val simultaneousPitchesToLilypond: LilypondFormat[NoteGroupGlyph] = noteGroup => {
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

  implicit val writableDurationToLilypond: LilypondFormat[NoteValue] = dur => {
    s"${dur.base.d}" + ("." * dur.dots)
  }

  implicit val timeSignatureToLilypond: LilypondFormat[TimeSignatureGlyph] = timeSignature => {
    val division = timeSignature.division
    s"\\time ${division.num}/${division.denom}"
  }

  implicit val keyToLilypond: LilypondFormat[KeyGlyph] = key => {
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
