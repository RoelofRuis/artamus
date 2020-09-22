package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.DisplayableMusic
import nl.roelofruis.artamus.core.layout.Glyph.{GlyphDuration, SingleGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, NoteStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.StaffGlyph.{NoteGroupGlyph, RestGlyph}
import nl.roelofruis.artamus.core.track.Pitched.{Octave, PitchDescriptor, Quality}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.lilypond.Document.DocumentWriter
import nl.roelofruis.artamus.lilypond.Model.LilypondSettings

trait LilypondFormatting extends TunedMaths with DocumentWriter {
  val settings: LilypondSettings

  def format(displayableMusic: DisplayableMusic): Document = {
    val contents: Seq[Document] = Seq(
      s"""\\version "${settings.lilypondVersion}"""",
      scoped("\\paper {", "}")(
        s"""#(set-paper-size "${settings.paperSize}")""",
        "ragged-last-bottom = ##f",
        "indent = 0.0"
      ),
      scoped("\\header {", "}")(
        "tagline = ##f"
      ),
      scoped("\\score {", "}")(
        writeStaffGroup(displayableMusic.staffGroup),
        scoped("\\layout {", "}")(),
        scoped("\\midi {", "}")(
          s"\\tempo 4 = ${settings.quarterTempo}",
          scoped("\\context {", "}")(
            "\\ChordNames \\remove Note_performer"
          )
        )
      ),
    )

    flat(contents: _*)
  }

  private def writeStaffGroup(staffGroup: StaffGroup): Document = {
    if (staffGroup.isEmpty) "s"
    else {
      scoped("<<", ">>")(
        staffGroup.map {
          case s: ChordStaff => writeChordStaff(s)
          case s: NoteStaff => writeNoteStaff(s)
          case _ => stringIsDocument("")
        }: _*
      )
    }
  }

  private def writeNoteStaff(staff: NoteStaff): Document = {
    val contents = staff.glyphs.map {
      case SingleGlyph(_: RestGlyph, duration) =>
        "r" + writeDuration(duration)
      case SingleGlyph(NoteGroupGlyph(Seq()), duration) =>
        "s" + writeDuration(duration)
      case SingleGlyph(NoteGroupGlyph(notes), duration) =>
        val tie = writeTie(duration)
        val writtenDuration = writeDuration(duration)
        if (notes.length == 1) {
          val writtenPitch = writePitchDescriptor(notes.head._1)
          val writtenOctave = writeOctave(notes.head._2)
          writtenPitch + writtenOctave + writtenDuration + tie
        } else {
          val writtenNotes = notes.map { case (descriptor, octave) =>
            writePitchDescriptor(descriptor) + writeOctave(octave) + tie
          }.mkString("<", " ", ">")
          writtenNotes + writtenDuration
        }
    }.mkString("\n")

    scoped("\\new Staff {", "}")(
      "\\numericTimeSignature",
      "\\override Score.BarNumber.break-visibility = ##(#f #t #f)",
      "\\set Score.barNumberVisibility = #all-bar-numbers-visible",
      "\\bar \"\"",
      contents,
      "\\bar \"|.\""
    )
  }

  private def writeChordStaff(staff: ChordStaff): Document = {
    val contents = staff.glyphs.map {
      case SingleGlyph(glyph: ChordNameGlyph, duration) =>
        val tie = writeTie(duration)
        val spelledRoot = writePitchDescriptor(glyph.root)
        val spelledDur = writeDuration(duration)
        val spelledQuality = writeQuality(glyph.quality)
        spelledRoot + spelledDur + spelledQuality + tie

      case SingleGlyph(_: ChordRestGlyph, duration) =>
        s"s${writeDuration(duration)}"

      case _ => //TODO: write tuplets
    }.mkString("\n")

    scoped("\\new ChordNames {", "}")(
      scoped("\\chordmode {", "}")(
        "\\set chordChanges = ##t",
        "\\override ChordName #'font-series = #'medium",
        contents
      )
    )
  }

  private def writeTie(duration: GlyphDuration): String = if (duration.tieToNext) "~" else ""

  private def writeOctave(octave: Octave): String = {
    // 3th midi octave is unaltered in lilypond notation
    octave - 3 match {
      case i if i == 0 => ""
      case i if i < 0 => "," * i
      case i if i > 0 => "'" * i
    }
  }

  private def writePitchDescriptor(descriptor: PitchDescriptor): String = {
    val accidentals = Seq(
      descriptor.pitchClass - settings.pitchClassSequence(descriptor.step),
      (descriptor.pitchClass - settings.numPitchClasses) - settings.pitchClassSequence(descriptor.step)
    ).minBy(Math.abs)

    writeStep(descriptor.step) + writeAccidentals(accidentals, descriptor.step)
  }

  private def writeStep(step: Int): String = settings.stepNames.lift(step).getOrElse("")

  private def writeAccidentals(i: Int, step: Int): String = {
    i match {
      case -1 if step == 2 || step == 5 => "s"
      case x if x > 0 => writeAccidentals(i - 1, step) + settings.sharpSpelling
      case x if x < 0 => writeAccidentals(i + 1, step) + settings.flatSpelling
      case 0 => ""
    }
  }

  private def writeDuration(glyphDuration: GlyphDuration): String = s"${2**glyphDuration.n}" + (settings.dotSpelling * glyphDuration.dots)

  private def writeQuality(quality: Quality): String = settings.qualitySpelling.get(quality).map(":" + _).getOrElse("")

}
