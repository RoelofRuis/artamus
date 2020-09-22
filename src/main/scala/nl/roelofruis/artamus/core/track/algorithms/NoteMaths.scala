package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.track.Pitched.Note

trait NoteMaths extends TunedMaths {

  implicit class NoteMath(note: Note) {
    val midiNr: Int = (note.octave + 1) * settings.numPitchClasses + note.descriptor.pitchClass
  }

  implicit class NoteSequenceMath(notes: Seq[Note]) {
    val orderedMidiNumbers: Seq[Int] = notes.map(_.midiNr).sorted
    val midiDifferences: Seq[Int] = orderedMidiNumbers.sliding(2).map { case Seq(a, b, _*) => b - a }.toSeq
  }

}
