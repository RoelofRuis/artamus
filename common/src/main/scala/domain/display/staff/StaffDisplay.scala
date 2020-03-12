package domain.display.staff

import domain.write.analysis.TwelveTonePitchSpelling
import domain.math.temporal.{Duration, Position, Window}
import domain.display.staff.Inclusion.InclusionStrategy
import domain.display.staff.StaffGlyph._
import domain.display.{Bars, NoteValues, StaffGroup}
import domain.write.{Keys, Notes, TimeSignatures}
import domain.primitives.{Key, Note, NoteGroup}

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
        RhythmicStaff(initialGlyphs() ++ read(Inclusion.all))
      )
    }

    def getNotes: StaffGroup = {
      val trebleGlyphs = read(Inclusion.higherNoteNumbers(59)).buffered
      val bassGlyphs = read(Inclusion.lowerEqualNoteNumbers(59)).buffered

      val hasTreble = trebleGlyphs.headOption.isEmpty
      val hasBass = bassGlyphs.headOption.isEmpty

      val staffGroup = (hasTreble, hasBass) match {
        case (true, true) =>
          GrandStaff(
            NoteStaff(Treble, initialGlyphs() ++ trebleGlyphs),
            NoteStaff(Bass, initialGlyphs() ++ bassGlyphs)
          )
        case (true, false) =>
          NoteStaff(Treble, initialGlyphs() ++ trebleGlyphs)
        case (false, true) =>
          NoteStaff(Bass, initialGlyphs() ++ bassGlyphs)
        case (false, false) =>
          NoteStaff(Treble, initialGlyphs())
      }

      StaffGroup(staffGroup)
    }

    private def initialGlyphs(): Iterator[StaffGlyph] = Iterator(
      TimeSignatureGlyph(disp.timeSignatures.initialTimeSignature.division),
      KeyGlyph(initialKey.root, initialKey.scale)
    )

    private def read(include: InclusionStrategy): Iterator[StaffGlyph] = {
      val groupsList = disp.notes.readGroupsList()
      def loop(cursor: Position, groups: List[NoteGroup]): Iterator[StaffGlyph] = {
        groups match {
          case group :: tail =>
            include(group) match {
              case Nil => loop(cursor, tail)
              case notes => calculateGlyphs(cursor, group.window, notes) ++ loop(group.window.end, tail)
            }

          case Nil =>
            val finalBarlinePos = disp.timeSignatures.nextBarLine(groupsList.lastOption.map(_.window.end).getOrElse(cursor))
            fillWithRests(cursor, finalBarlinePos)
        }
      }
      loop(Position.ZERO, groupsList)
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
