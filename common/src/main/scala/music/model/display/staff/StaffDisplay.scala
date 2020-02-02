package music.model.display.staff

import music.analysis.TwelveTonePitchSpelling
import music.math.temporal.{Duration, Position, Window}
import music.model.display.staff.Inclusion.InclusionStrategy
import music.model.display.staff.StaffGlyph._
import music.model.display.{Bars, NoteValues, StaffGroup}
import music.model.write.{Keys, Notes, TimeSignatures}
import music.primitives.{Key, Note, NoteGroup}

object StaffDisplay {

  import Bars._
  import NoteValues._

  final case class StaffDisplayable(
    timeSignatures: TimeSignatures,
    keys: Keys,
    notes: Notes
  )

  implicit class StaffDisplayableOps(disp: StaffDisplayable) {

    private val initialKey: Key = disp.keys.initialKey

    // TODO: dynamic reading window

    def getRhythm: StaffGroup = {
      StaffGroup(
        RhythmicStaff(initialElements() ++ read(Inclusion.all))
      )
    }

    def getNotes: StaffGroup = {
      StaffGroup(
        GrandStaff(
          NoteStaff(Treble, initialElements() ++ read(Inclusion.higherNoteNumbers(59))),
          NoteStaff(Bass, initialElements() ++ read(Inclusion.lowerEqualNoteNumbers(59)))
        )
      )
    }

    private def initialElements(): Iterator[StaffGlyph] = Iterator(
      TimeSignatureGlyph(disp.timeSignatures.initialTimeSignature.division),
      KeyGlyph(initialKey.root, initialKey.scale)
    )

    private def read(include: InclusionStrategy): Iterator[StaffGlyph] = {
      def loop(cursor: Position, groups: List[NoteGroup]): Iterator[StaffGlyph] = {
        groups match {
          case Nil => Iterator(FullBarRestGlyph(1))

          case group :: Nil =>
            include(group) match {
              case Nil =>
                val nextBarLinePos = disp.timeSignatures.nextBarLine(group.window.end)
                fillWithRests(cursor, nextBarLinePos)

              case notes =>
                val glyphs = calculateGlyphs(cursor, group.window, notes)
                val nextBarLine = disp.timeSignatures.nextBarLine(group.window.end)
                val finalRests = fillWithRests(group.window.end, nextBarLine)
                glyphs ++ finalRests
            }

          case group :: tail =>
            include(group) match {
              case Nil => loop(cursor, tail)
              case notes => calculateGlyphs(cursor, group.window, notes) ++ loop(group.window.end, tail)
            }
        }
      }
      loop(Position.ZERO, disp.notes.readGroupsList())
    }

    private def calculateGlyphs(cursor: Position, noteWindow: Window, notes: Seq[Note]): Iterator[StaffGlyph] = {
      val restGlyphs = fillWithRests(cursor, noteWindow.start)

      val noteDurations = disp
        .timeSignatures
        .fit(noteWindow)
        .flatMap(_.duration.asNoteValues)

      val pitches = notes.map(TwelveTonePitchSpelling.spellNote(_, initialKey))

      val noteGlyphs = noteDurations
        .zipWithIndex
        .map { case (dur, i) => NoteGroupGlyph(dur, pitches, i != (noteDurations.size - 1)) }
        .iterator

      restGlyphs ++ noteGlyphs
    }

    private def fillWithRests(from: Position, to: Position): Iterator[StaffGlyph] = to - from match {
      case Duration.ZERO => Iterator.empty
      case d => disp
        .timeSignatures
        .fit(Window(from, d))
        .flatMap(_.duration.asNoteValues)
        .map(RestGlyph(_, silent=false))
        .iterator
    }

  }

}
