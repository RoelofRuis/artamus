package music.analysis

import music.symbol.collection.TrackSymbol
import music.primitives._
import music.symbol.{Key, Note}

object TwelveTonePitchSpelling {

  import music.analysis.TwelveToneEqualTemprament._

  def spellNotes(notes: Seq[TrackSymbol[Note]], keyOption: Option[Key]): Seq[TrackSymbol[Note]] = {
    val key = keyOption.getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))
    notes.map(note => note.update(note.symbol.withScientificPitch(spell(note.symbol, key))))
  }

  private def spell(note: Note, key: Key): ScientificPitch = {
    val spelledPitch = spellPc(note.pitchClass, key)

    val newOctave = if (spelledPitch.span > tuning.span) Octave(note.octave.value - 1) else note.octave

    ScientificPitch(spelledPitch, newOctave)
  }

  private def spellPc(pc: PitchClass, key: Key): PitchSpelling = {
    tuning
      .possibleIntervals(key.root.toPc, pc)
      .map(i => key.root.addInterval(i))
      .toSeq
      .minBy(_.accidental.value.abs) // TODO: this decision might be more complex...
  }

}
