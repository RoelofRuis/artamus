package domain.display.staff

import domain.primitives.{MidiNoteNumber, Note, NoteGroup}

object Inclusion {

  trait InclusionStrategy extends (NoteGroup => Seq[Note])

  import domain.write.analysis.TwelveToneTuning._

  def higherNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
    noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value > bound)

  def lowerEqualNoteNumbers(bound: Int): InclusionStrategy = noteGroup =>
    noteGroup.notes.filter(note => MidiNoteNumber(note.octave, note.pitchClass).value <= bound)

  def all: InclusionStrategy = noteGroup => noteGroup.notes

}