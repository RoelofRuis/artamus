package music.model.display.staff

import music.analysis.TwelveTonePitchSpelling
import music.math.temporal.{Position, Window}
import music.model.display.staff.StaffGlyph.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import music.model.display.{Bars, NoteValues}
import music.model.write.track.Track
import music.primitives.{Key, NoteGroup, ScientificPitch}

object StaffDisplay {

  import Bars._
  import NoteValues._

  implicit class StaffDisplayOps(track: Track) {

    private val initialKey: Key = track.keys.initialKey

    def getStaves: (Staff, Staff) = {
      // TODO: dynamic reading window
      val window = Window.instantAt(Position.ZERO)

      val initialElements = Iterator(
        TimeSignatureGlyph(track.timeSignatures.initialTimeSignature.division),
        KeyGlyph(initialKey.root, initialKey.scale)
      )

      (
        Staff(
          Treble,
          initialElements ++ read(
            window,
            track.notes.readGroups,
            InclusionStrategies.higherNoteNumbers(59)
          )
        ),
        Staff(
          Bass,
          initialElements ++ read(
            window,
            track.notes.readGroups,
            InclusionStrategies.lowerEqualNoteNumbers(59)
          )
        )
      )
    }

    def read(window: Window, notes: Iterator[NoteGroup], include: InclusionStrategy): Iterator[StaffGlyph] = {
      notes.nextOption() match {
        case None =>
          track.timeSignatures.fillBarFrom(window).duration
            .asNoteValues
            .map(RestGlyph(_, silent=false))
            .iterator

        case Some(nextGroup) if include(nextGroup).isEmpty =>
          // windowing
          val nextWindow = Window(window.start, window.start - nextGroup.window.end)
          println("Empty include" + nextWindow)
          read(nextWindow, notes, include)

        case Some(nextGroup) =>
          // windowing
          val nextWindow = nextGroup.window
          println("Does include" + nextWindow)
          val nextNotes = include(nextGroup)

          // rests
          val rests = window.until(nextWindow) match {
            case None => Iterator.empty
            case Some(diff) =>
              track
                .timeSignatures
                .fit(diff)
                .flatMap(_.duration.asNoteValues)
                .map(RestGlyph(_, silent=false))
                .iterator
          }

          // pitches
          // TODO: Use the 'active' key instead of initial key.
          val pitches: Seq[ScientificPitch] = nextNotes.map(TwelveTonePitchSpelling.spellNote(_, initialKey))
          val fittedDurations =
            track
              .timeSignatures
              .fit(nextWindow)
              .flatMap(_.duration.asNoteValues)

          val lilyStrings = fittedDurations
            .zipWithIndex
            .map { case (dur, i) => NoteGroupGlyph(dur, pitches, i != (fittedDurations.size - 1)) }
            .iterator

          if (lilyStrings.isEmpty) rests ++ read(nextWindow, notes, include)
          else rests ++ lilyStrings ++ read(nextWindow, notes, include)
      }
    }
  }

}
