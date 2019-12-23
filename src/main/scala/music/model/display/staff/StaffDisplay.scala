package music.model.display.staff

import music.analysis.TwelveTonePitchSpelling
import music.math.temporal.{Position, Window}
import music.model.display.staff.Inclusion.InclusionStrategy
import music.model.display.staff.StaffGlyph.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import music.model.display.{Bars, NoteValues}
import music.model.write.track.Track
import music.primitives.{Key, ScientificPitch}

object StaffDisplay {

  import Bars._
  import NoteValues._

  implicit class StaffDisplayOps(track: Track) {

    private val initialKey: Key = track.keys.initialKey

    // TODO: dynamic reading window
    def getStaves: (Staff, Staff) = {
      val window = Window.instantAt(Position.ZERO)

      (
        Staff(Treble, initialElements() ++ read(window, Inclusion.higherNoteNumbers(59))),
        Staff(Bass, initialElements() ++ read(window, Inclusion.lowerEqualNoteNumbers(59)))
      )
    }

    private def initialElements(): Iterator[StaffGlyph] = Iterator(
      TimeSignatureGlyph(track.timeSignatures.initialTimeSignature.division),
      KeyGlyph(initialKey.root, initialKey.scale)
    )

    private def read(window: Window, include: InclusionStrategy): Iterator[StaffGlyph] = {
      val notes = track.notes.readGroups()
      def loop(lastWindow: Window): Iterator[StaffGlyph] = {
        notes.nextOption() match {
          case None =>
            val finalWindow = track.timeSignatures.extendToFillBar(lastWindow)
            if (finalWindow.isInstant) Iterator.empty
            else {
              track
                .timeSignatures
                .fit(finalWindow)
                .flatMap(_.duration.asNoteValues)
                .map(RestGlyph(_, silent=false))
                .iterator
            }

          case Some(nextGroup) if include(nextGroup).isEmpty =>
            // windowing
            val nextWindow = lastWindow.spanning(nextGroup.window)
            loop(nextWindow)

          case Some(nextGroup) =>
            // windowing
            val nextWindow = nextGroup.window
            val nextNotes = include(nextGroup)

            // rests
            val rests = lastWindow.until(nextWindow) match {
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

            if (lilyStrings.isEmpty) rests ++ loop(Window.instantAt(nextWindow.end))
            else rests ++ lilyStrings ++ loop(Window.instantAt(nextWindow.end))
        }
      }
      loop(window)
    }
  }

}
