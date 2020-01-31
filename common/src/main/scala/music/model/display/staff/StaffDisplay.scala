package music.model.display.staff

import music.analysis.TwelveTonePitchSpelling
import music.math.temporal.{Duration, Position, Window}
import music.model.display.staff.Inclusion.InclusionStrategy
import music.model.display.staff.StaffGlyph._
import music.model.display.{Bars, NoteValues, StaffGroup}
import music.model.write.Track
import music.primitives.{Key, Note, NoteGroup}

object StaffDisplay {

  import Bars._
  import NoteValues._

  implicit class StaffDisplayOps(track: Track) {

    private val initialKey: Key = track.keys.initialKey

    // TODO: dynamic reading window
    def getNoteStaffGroup: StaffGroup = {
      StaffGroup(
        GrandStaff(
          NoteStaff(Treble, initialElements() ++ read(Inclusion.higherNoteNumbers(59))),
          NoteStaff(Bass, initialElements() ++ read(Inclusion.lowerEqualNoteNumbers(59)))
        )
      )
    }

    private def initialElements(): Iterator[StaffGlyph] = Iterator(
      TimeSignatureGlyph(track.timeSignatures.initialTimeSignature.division),
      KeyGlyph(initialKey.root, initialKey.scale)
    )

    private def read(include: InclusionStrategy): Iterator[StaffGlyph] = {
      def loop(cursor: Position, groups: List[NoteGroup]): Iterator[StaffGlyph] = {
        groups match {
          case Nil => Iterator(FullBarRestGlyph(1))

          case group :: Nil =>
            include(group) match {
              case Nil =>
                val nextBarLinePos = track.timeSignatures.nextBarLine(group.window.end)
                fillWithRests(cursor, nextBarLinePos)

              case notes =>
                val glyphs = calculateGlyphs(cursor, group.window, notes)
                val nextBarLine = track.timeSignatures.nextBarLine(group.window.end)
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
      loop(Position.ZERO, track.notes.readGroupsList())
    }

    private def calculateGlyphs(cursor: Position, noteWindow: Window, notes: Seq[Note]): Iterator[StaffGlyph] = {
      val restGlyphs = fillWithRests(cursor, noteWindow.start)

      val noteDurations = track
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
      case d => track
        .timeSignatures
        .fit(Window(from, d))
        .flatMap(_.duration.asNoteValues)
        .map(RestGlyph(_, silent=false))
        .iterator
    }

  }

}
