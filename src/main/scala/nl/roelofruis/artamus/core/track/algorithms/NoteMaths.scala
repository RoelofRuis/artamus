package nl.roelofruis.artamus.core.track.algorithms

import nl.roelofruis.artamus.core.track.Pitched.{Note, NoteGroup, Octave}

trait NoteMaths extends TunedMaths {

  implicit class OctaveMath(octave: Octave) {
    val midiNr: Int = (octave + 1) * settings.numPitchClasses
  }

  implicit class NoteMath(note: Note) {
    val midiNr: Int = note.octave.midiNr + note.descriptor.pitchClass
  }

  implicit class NoteGroupMath(notes: NoteGroup) {
    /** Lowest to highest ordered midi numbers */
    val orderedMidiNumbers: Seq[Int] = notes.map(_.midiNr).sorted
    /** Midi differences between the sorted notes. */
    val orderedMidiDifferences: Seq[Int] = orderedMidiNumbers.sliding(2).map { case Seq(a, b, _*) => b - a }.toSeq
  }

}
