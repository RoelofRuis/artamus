package server.interpret.lilypond

import music.collection.TrackSymbol
import music.primitives._
import music.spelling.{SpelledNote, SpelledPitch}
import music.symbols.{Key, Note}

// TODO: move to algorithm
object Spelling {

  import music.analysis.TwelveToneEqualTemprament._

  def spelledNotes(notes: Seq[TrackSymbol[Note]], key: Key): Seq[SpelledNote] = {
    notes.map(note => spellNote(note.symbol, key))
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
