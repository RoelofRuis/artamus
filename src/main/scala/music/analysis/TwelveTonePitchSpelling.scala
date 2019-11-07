package music.analysis

import music.symbol.collection.TrackSymbol
import music.primitives._
import music.symbol.{Chord, Key, Note}

object TwelveTonePitchSpelling {

  import music.analysis.TwelveToneTuning._

  def spellNotes(notes: Seq[TrackSymbol[Note]], keyOption: Option[Key]): Seq[TrackSymbol[Note]] = {
    val key = keyOption.getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))
    notes.map(note => note.update(note.symbol.withScientificPitch(spell(note.symbol, key))))
  }

  def spellChord(chord: TrackSymbol[Chord], keyOption: Option[Key]): TrackSymbol[Chord] = {
    val key = keyOption.getOrElse(Key(PitchSpelling(Step(0), Accidental(0)), Scale.MAJOR))
    chord.update(chord.symbol.withRootSpelling(spellPc(chord.symbol.root, key)))
  }

  private def spell(note: Note, key: Key): ScientificPitch = {
    val spelledPitch = spellPc(note.pitchClass, key)

    val newOctave = if (spelledPitch.span > tuning.span) Octave(note.octave.value - 1) else note.octave

    ScientificPitch(spelledPitch, newOctave)
  }

  private def spellPc(pc: PitchClass, key: Key): PitchSpelling = {
    val keyPcs = key.scale.stepSizes

    tuning
      .possibleIntervals(key.root.toPc, pc)
      .map { interval =>
        val spelling = key.root.addInterval(interval)
        val keyScore = if (keyPcs.contains(spelling.toPc.value)) 0 else 1
        val accidentalScore = spelling.accidental.value.abs
        (keyScore, accidentalScore, spelling)
      }
      .minBy { case (keyScore, accidentalScore, _) => (keyScore, accidentalScore) }._3
  }

}
