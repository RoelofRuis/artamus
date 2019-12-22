package music.model.display.staff

import music.primitives.{MidiNoteNumber, Note, NoteGroup}

trait InclusionStrategy extends (NoteGroup => Seq[Note])

object InclusionStrategies {

  import music.analysis.TwelveToneTuning._

  def higherNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
    noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value > bound)

  def lowerEqualNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
    noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value <= bound)
}