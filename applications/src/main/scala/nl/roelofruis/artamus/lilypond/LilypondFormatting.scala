package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.application.rendering.Model.LilypondSettings
import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.DisplayableMusic
import nl.roelofruis.artamus.core.layout.Glyph.{GlyphDuration, SingleGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, StaffGroup}
import nl.roelofruis.artamus.core.track.Pitched.{PitchDescriptor, Quality}
import nl.roelofruis.artamus.core.track.analysis.TunedMaths
import nl.roelofruis.artamus.lilypond.Document.DocumentWriter

trait LilypondFormatting extends TunedMaths with DocumentWriter {
  val settings: LilypondSettings

  def format(displayableMusic: DisplayableMusic): Document = {
    flat(
      s"""\\version "${settings.lilypondVersion}"""",
      scoped("\\paper {", "}")(
        s"""#(set-paper-size "${settings.paperSize}")"""
      ),
      scoped("\\header {", "}")(
        "tagline = ##f"
      ),
      scoped("\\score {", "}")(
        writeStaffGroup(displayableMusic.staffGroup)
      )
    )
  }

  private def writeStaffGroup(staffGroup: StaffGroup): Document = {
    if (staffGroup.isEmpty) "s"
    else {
      staffGroup.map {
        case s: ChordStaff => writeChordStaff(s)
        case _ => ""
      }.mkString("<<", "", "\n>>")
    }
  }

  private def writeChordStaff(staff: ChordStaff): String = {
    val contents = staff.glyphs.map {
      case SingleGlyph(glyph: ChordNameGlyph, duration) =>
        val spelledRoot = writePitchDescriptor(glyph.root)
        val spelledDur = writeDuration(duration)
        val spelledQuality = writeQuality(glyph.quality)
        spelledRoot + spelledDur + spelledQuality

      case SingleGlyph(_: ChordRestGlyph, duration) =>
        s"s${writeDuration(duration)}"

      case _ => //TODO: write tuplets
    }.mkString("\n")

    s"""\\new ChordNames {
       |\\chordmode {
       |\\override ChordName #'font-series = #'medium'
       |$contents
       |}
       |}""".stripMargin
  }

  private def writePitchDescriptor(descriptor: PitchDescriptor): String = {
    val accidentals = descriptor.pitchClass - settings.pitchClassSequence(descriptor.step)

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
