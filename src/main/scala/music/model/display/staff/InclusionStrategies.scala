package music.model.display.staff

import music.primitives.{MidiNoteNumber, Note, NoteGroup}

object InclusionStrategies {

  trait InclusionStrategy extends (NoteGroup => Seq[Note])

  import music.analysis.TwelveToneTuning._

  def higherNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
    noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value > bound)

  def lowerEqualNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
    noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value <= bound)
}