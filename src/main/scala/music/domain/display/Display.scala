package music.domain.display

import music.analysis.{NoteValueConversion, TwelveTonePitchSpelling}
import music.domain.display.chord.ChordStaffGlyph.{ChordNameGlyph, ChordRestGlyph}
import music.domain.display.chord.{ChordStaff, ChordStaffGlyph}
import music.domain.display.staff.{Staff, StaffGlyph}
import music.domain.display.staff.StaffGlyph.{KeyGlyph, NoteGroupGlyph, RestGlyph, TimeSignatureGlyph}
import music.domain.write.track.Track
import music.math.temporal.{Position, Window}
import music.domain.primitives.{Key, ScientificPitch}

object Display {

  import Bars._

  def displayTrack(track: Track): DisplayTrack = {
    DisplayTrack(
      track.getStaff,
      track.getChordStaff
    )
  }

  implicit class StaffDisplay(track: Track) {

    private val notes = track.notes.readGroups
    private val initialKey: Key = track.keys.initialKey

    def getStaff: Staff = {
      // TODO: dynamic reading window
      val window = Window.instantAt(Position.ZERO)

      val initialElements = Iterator(
        TimeSignatureGlyph(track.timeSignatures.initialTimeSignature.division),
        KeyGlyph(initialKey.root, initialKey.scale)
      )

      Staff(initialElements ++ read(window))
    }

    private def read(window: Window): Iterator[StaffGlyph] = {
      notes.nextOption() match {
        case None =>
          NoteValueConversion
            .from(track.timeSignatures.fillBarFrom(window).duration)
            .map(RestGlyph(_, silent=false))
            .iterator

        case Some(nextGroup) =>
          // windowing
          val nextWindow = nextGroup.window

          // rests
          val rests = window.until(nextWindow) match {
            case None => Iterator.empty
            case Some(diff) =>
              track
                .timeSignatures
                .fit(diff)
                .flatMap(window => NoteValueConversion.from(window.duration))
                .map(RestGlyph(_, silent=false))
                .iterator
          }

          // pitches
          // TODO: Use the 'active' key instead of initial key.
          val pitches: Seq[ScientificPitch] = nextGroup.notes.map(TwelveTonePitchSpelling.spellNote(_, initialKey))
          val fittedDurations =
            track
              .timeSignatures
              .fit(nextWindow)
              .flatMap(window => NoteValueConversion.from(window.duration))

          val lilyStrings = fittedDurations
            .zipWithIndex
            .map { case (dur, i) => NoteGroupGlyph(dur, pitches, i != (fittedDurations.size - 1)) }
            .iterator

          if (lilyStrings.isEmpty) rests ++ read(nextWindow)
          else rests ++ lilyStrings ++ read(nextWindow)
      }
    }
  }

  implicit class ChordStaffDisplay(track: Track) {

    private val chords = track.chords.read
    private val initialKey = track.keys.initialKey

    def getChordStaff: ChordStaff = {
      // TODO: dynamic reading window
      val window = Window.instantAt(Position.ZERO)
      ChordStaff(read(window))
    }

    private def read(window: Window): Iterator[ChordStaffGlyph] = {
      chords.nextOption match {
        case None => Iterator.empty

        case Some((nextWindow, nextChord)) =>
          val writeableChords = {
            // TODO: Use the 'active' key instead of initial key.
            val spelling = TwelveTonePitchSpelling.spellChord(nextChord, initialKey)
            val written = NoteValueConversion.from(nextWindow.duration) match {
              case Nil => Seq()
              case head :: Nil =>
                ChordNameGlyph(head, spelling, nextChord.functions) :: Nil
              case head :: tail =>
                ChordNameGlyph(head, spelling, nextChord.functions) :: tail.map(ChordRestGlyph)
            }
            written.iterator
          }

          val rests = window.until(nextWindow) match {
            case None => Iterator.empty
            case Some(diff) =>
              track
                .timeSignatures
                .fit(diff)
                .flatMap(window => NoteValueConversion.from(window.duration))
                .map(ChordRestGlyph)
                .iterator
          }

          rests ++ writeableChords ++ read(nextWindow)
      }
    }

  }

}
