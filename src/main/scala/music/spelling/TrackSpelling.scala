package music.spelling

import music.collection.Track
import music.primitives._
import music.symbols.{Chord, Key, Note, TimeSignature}

object TrackSpelling {

  import music.analysis.TwelveToneEqualTemprament._

  implicit class SpellingOps(track: Track) {

    // TODO: read from 'any' position
    def timeSignatureAtZero: Option[TimeSignature] = {
      track
        .getSymbolTrack[TimeSignature]
        .readAt(Position.zero)
        .reverse
        .map(_.symbol)
        .headOption
    }

    // TODO: read from 'any' position
    def keyAtZero: Option[Key] = {
      track
        .getSymbolTrack[Key]
        .readAt(Position.zero)
        .reverse
        .map(_.symbol)
        .headOption
    }

    def spelledNotes: Seq[Seq[SpelledNote]] = {
      val key = keyAtZero.getOrElse(Key(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR))

      track
        .getSymbolTrack[Note]
        .readAllGrouped
        .map(_.map(note => spellNote(note.symbol, key)))
    }

    def spelledChords: Seq[SpelledChord] = {
      val key = keyAtZero.getOrElse(Key(SpelledPitch(Step(0), Accidental(0)), Scale.MAJOR))

      track
        .getSymbolTrack[Chord]
        .readAll.flatMap { chord => spellChord(chord.symbol, key) }
    }

    private def spellChord(chord: Chord, key: Key): Option[SpelledChord] = {
      for {
        dur <- chord.duration
      } yield SpelledChord(dur, spellPc(chord.root, key))
    }

    private def spellNote(note: Note, key: Key): SpelledNote = {
      val spelledPitch = spellPc(note.pitchClass, key)

      val newOctave = if (spelledPitch.span > tuning.span) Octave(note.octave.value - 1) else note.octave

      SpelledNote(
        note.duration,
        newOctave,
        spelledPitch
      )
    }

    private def spellPc(pc: PitchClass, key: Key): SpelledPitch = {
      tuning
        .possibleIntervals(key.root.toPc, pc)
        .map(i => key.root.addInterval(i))
        .toSeq
        .minBy(_.accidental.value.abs)
    }
  }

}
