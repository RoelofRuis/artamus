package nl.roelofruis.artamus.lilypond

import nl.roelofruis.artamus.core.common.Maths._
import nl.roelofruis.artamus.core.layout.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import nl.roelofruis.artamus.core.layout.DisplayableMusic
import nl.roelofruis.artamus.core.layout.Glyph.{GlyphDuration, InstantGlyph, SingleGlyph}
import nl.roelofruis.artamus.core.layout.RNAStaffGlyph.{DegreeGlyph, RNARestGlyph}
import nl.roelofruis.artamus.core.layout.Staff.{ChordStaff, NoteStaff, RNAStaff, StaffGroup}
import nl.roelofruis.artamus.core.layout.StaffGlyph.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import nl.roelofruis.artamus.core.track.Pitched.{Octave, PitchDescriptor, Quality, Scale}
import nl.roelofruis.artamus.core.track.algorithms.TunedMaths
import nl.roelofruis.artamus.document._

trait LilypondFormatting extends TunedMaths {
  val settings: LilypondSettings

  def format(displayableMusic: DisplayableMusic): Document = {
    var contents: Seq[Document] = Seq(
      s"""\\version "${settings.lilypondVersion}"""",
    )

    if (displayableMusic.staffGroup.collectFirst { case _: RNAStaff => () }.isDefined) {
      contents ++= Seq("\\include \"../templates/roman_numerals.ly\"")
    }

    contents ++= Seq(
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
        scoped("\\layout {", "}")(
          scoped("\\context {", "}")(
            "\\Staff",
            "\\RemoveEmptyStaves",
            "\\numericTimeSignature",
            "\\override VerticalAxisGroup.remove-first = ##t"
          )
        ),
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
          case s: RNAStaff => writeRNAStaff(s)
          case _ => stringIsDocument("")
        }: _*
      )
    }
  }

  private def writeRNAStaff(staff: RNAStaff): Document = {
    val aligner = staff.glyphs.map {
      case SingleGlyph(DegreeGlyph(_), duration) =>
        "c" + writeDuration(duration)
      case SingleGlyph(_: RNARestGlyph, duration) =>
        writeSilentRest(duration)
      case _ =>
    }.mkString("\n")

    val contents = staff.glyphs.map {
      case SingleGlyph(DegreeGlyph(degree), _) =>
        val baseName = writeRomanNumeral(degree.root)
        val relativeName = degree.relativeTo.map(d => " / " + writeRomanNumeral(d)).getOrElse("")
        val tritoneSub   = if (degree.tritoneSub) "T" else ""
        val qualityName  = settings.degreeQualitySpelling.getOrElse(degree.quality, "")

        s"\\markup \\rN { $baseName $tritoneSub$qualityName$relativeName }"

      case SingleGlyph(_: RNARestGlyph, duration) =>
        writeSilentRest(duration)

      case _ =>
    }.mkString("\n")

    flat(
      scoped("\\new NullVoice = \"rna-aligner\" {", "}")(
        aligner
      ),
      scoped("\\new Lyrics \\lyricsto \"rna-aligner\" {", "}")(
        scoped("\\lyricmode {", "}")(
          contents
        )
      )
    )
  }

  private def writeRomanNumeral(descriptor: PitchDescriptor): String = {
    val baseName = settings.degreeNames(descriptor.step)
    val accidental = descriptor.accidentalValue match {
      case i if i < 1 => "f" * -i
      case i if i > 1 => "s" * i
      case _ => ""
    }
    s"$accidental$baseName"
  }

  private def writeNoteStaff(staff: NoteStaff): Document = {
    val contents = staff.glyphs.map {
      case SingleGlyph(_: RestGlyph, duration) =>
        writeRest(duration)

      case SingleGlyph(NoteGroupGlyph(Seq()), duration) =>
        writeSilentRest(duration)

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
      case InstantGlyph(TimeSignatureGlyph(num, denom)) =>
        writeTimeSignature(num, denom)
      case InstantGlyph(KeyGlyph(root, scale)) =>
        writeKey(root, scale)
    }.mkString("\n")

    scoped("\\new Staff {", "}")(
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
        writeSilentRest(duration)

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

  private def writeKey(root: PitchDescriptor, scale: Scale): String = {
    // TODO: implement http://lilypond.org/doc/v2.18/Documentation/notation/displaying-pitches#key-signature
    settings.scaleSpelling
      .get(scale)
      .map { scaleName =>
        val rootName = writePitchDescriptor(root)
        s"\\key $rootName \\$scaleName"
      }.getOrElse("")
  }

  private def writeTimeSignature(num: Int, denom: FractionalPowerOfTwo): String = s"\\time $num/$denom"

  private def writeRest(duration: GlyphDuration): String = "r" + writeDuration(duration)

  private def writeSilentRest(duration: GlyphDuration): String = "s" + writeDuration(duration)

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
    writeStep(descriptor.step) + writeAccidentals(descriptor.accidentalValue, descriptor.step)
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

  private def writeQuality(quality: Quality): String = settings.chordQualitySpelling.get(quality).map(":" + _).getOrElse("")

}
