package music.spelling

import music.primitives._
import music.symbols.{Chord, Key, Note}

object PitchSpelling {

  import music.analysis.TwelveToneEqualTemprament._

  def spellChord(chord: Chord, key: Key): Option[SpelledChord] = {
    for {
      dur <- chord.duration
    } yield SpelledChord(dur, spellPc(chord.root, key))
  }

  def spellNote(note: Note, key: Key): SpelledNote = {
    SpelledNote(
      note.duration,
      note.octave,
      spellPc(note.pitchClass, key)
    )
  }

  private def spellPc(pc: PitchClass, key: Key): SpelledPitch = {
    val rootPc = key.root.toPc

    tuning
      .possibleIntervals(rootPc, pc)
      .map(i => key.root.addInterval(i))
      .toSeq
      .minBy(_.accidental.value.abs)
  }

}
