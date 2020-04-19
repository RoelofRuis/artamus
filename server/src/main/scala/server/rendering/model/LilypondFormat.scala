package server.rendering.model

import domain.display.glyph.ChordStaffGlyphFamily.{ChordNameGlyph, ChordRestGlyph}
import domain.display.staff.NoteStaff.{Bass, Clef, Treble}
import domain.display.glyph.StaffGlyphFamily.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import domain.display.staff._
import domain.display.glyph.Glyphs.{GlyphDuration, SingleGlyph}
import domain.math.IntegerMath
import domain.primitives._
import domain.write.analysis.TwelveToneTuning.TwelveToneFunctions

import scala.annotation.tailrec

private[rendering] trait LilypondFormat[A] {
  def toLilypond(a: A): String
}

private[rendering] object LilypondFormat {

  def apply[A](implicit formatter: LilypondFormat[A]): LilypondFormat[A] = formatter

  implicit class LilypondFormatOps[A : LilypondFormat](a: A) {
    def toLilypond: String = LilypondFormat[A].toLilypond(a)
  }

  implicit val staffGroupFormat: LilypondFormat[StaffGroup] = staffGroup => {
    if (staffGroup.staves.isEmpty) "s"
    else {
      staffGroup.staves.map {
        case s: NoteStaff => s.toLilypond
        case s: ChordStaff => s.toLilypond
        case s: GrandStaff => s.toLilypond
        case s: RhythmicStaff => s.toLilypond
        case _ => ""
      }.mkString("<<", "", "\n>>")
    }
  }

  implicit val rhythmicStaffFormat: LilypondFormat[RhythmicStaff] = staff => {
    val contents = staff.glyphs.map {
      case SingleGlyph(NoteGroupGlyph(_), duration) =>
        if (duration.tieToNext) s"c${writeDuration(duration)}~" else s"c${writeDuration(duration)}"

      case SingleGlyph(RestGlyph(), duration) =>
        s"r${writeDuration(duration)}"

      case SingleGlyph(t: TimeSignatureGlyph, _) => t.toLilypond

      case _ => "" // Tuplets!
    }.mkString("\n")
    s"""\\new RhythmicStaff {
       |\\numericTimeSignature
       |\\override Score.BarNumber.break-visibility = ##(#f #t #t)
       |\\set Score.barNumberVisibility = #all-bar-numbers-visible
       |\\bar ""
       |${contents}
       |}""".stripMargin
  }

  implicit val grandStaffFormat: LilypondFormat[GrandStaff] = staff => {
    s"""\\new GrandStaff <<
       |${staff.upper.toLilypond}
       |${staff.lower.toLilypond}
       |>>
       |""".stripMargin
  }

  implicit val noteStaffFormat: LilypondFormat[NoteStaff] = staff => {
    val contents = staff.glyphs.map {
      case SingleGlyph(noteGroup @ NoteGroupGlyph(notes), duration) =>
        if (notes.isEmpty) ""
        else {
          val tie = if (duration.tieToNext) "~" else ""
          if (noteGroup.isChord) {
            val lilyNotes = noteGroup.notes
              .map { pitch => pitch.spelling.toLilypond + pitch.octave.toLilypond + tie }
              .mkString("<", " ", ">")

            lilyNotes + writeDuration(duration)
          } else {
            val pitchSpelling = noteGroup.notes.head.spelling.toLilypond
            val octaveSpelling = noteGroup.notes.head.octave.toLilypond
            val durationSpelling = writeDuration(duration)
            pitchSpelling + octaveSpelling + durationSpelling + tie
          }
        }

      case SingleGlyph(RestGlyph(), duration) =>
        s"r${writeDuration(duration)}"

      case SingleGlyph(k: KeyGlyph, _) => k.toLilypond

      case SingleGlyph(t: TimeSignatureGlyph, _) => t.toLilypond

      case _ => "" // Tuplets!
    }.mkString("\n")
    s"""\\new Staff {
       |\\numericTimeSignature
       |\\override Score.BarNumber.break-visibility = ##(#f #t #t)
       |\\set Score.barNumberVisibility = #all-bar-numbers-visible
       |\\bar ""
       |${staff.clef.toLilypond}
       |$contents
       |\\bar "|."
       |}""".stripMargin
  }

  implicit val clefFormat: LilypondFormat[Clef] = {
    case Treble => "\\clef treble"
    case Bass => "\\clef bass"
  }

  implicit val chordStaffFormat: LilypondFormat[ChordStaff] = staff => {
    val contents = staff.glyphs.map {
      case SingleGlyph(glyph: ChordNameGlyph, duration) =>
        val spelledRoot = glyph.root.toLilypond
        val spelledDur = writeDuration(duration)
        spelledRoot + spelledDur + glyph.functions.toLilypond

      case SingleGlyph(_: ChordRestGlyph, duration) =>
        s"s${writeDuration(duration)}"

      case _ => // TODO: write tuplets
    }.mkString("\n")

    // TODO: implement better font (http://lilypond-frogs.2124236.n2.nabble.com/Changing-Chord-Name-Font-Size-td4008276.html)

    s"""\\new ChordNames {
       |\\chordmode {
       |\\override ChordName #'font-series = #'medium'
       |$contents
       |}
       |}""".stripMargin
  }

  implicit val chordFunctionsToLilypond: LilypondFormat[Set[Function]] = { functions =>
    if (functions.contains(TwelveToneFunctions.FLAT_THREE)) ":m"
    else ""
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
      case i if i < 0 => "," * -i
      case i if i > 0 => "'" * i
    }
  }

  def writeDuration(glyphDuration: GlyphDuration): String = {
    s"${2**glyphDuration.n}" + ("." * glyphDuration.dots)
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
