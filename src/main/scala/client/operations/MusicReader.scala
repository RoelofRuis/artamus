package client.operations

import javax.inject.Inject
import midi.in.MidiMessageReader
import music.{Accidental, MidiPitch, MusicVector, Scale}

class MusicReader @Inject() (reader: MidiMessageReader) {

  import midi.in.Reading._

  def readMusicVector: MusicVector = {
    val midiPitches = readMidiPitch(2)
    val firstStep = Scale.MAJOR_SCALE_MATH.pitchClassToStep(MidiPitch(midiPitches.head).pitchClass)

    if (firstStep.isEmpty) readMusicVector
    else {
      val diff = midiPitches.last - midiPitches.head
      MusicVector(firstStep.get, Accidental(diff))
    }
  }

  def readMidiPitch(n: Int): List[Int] = reader.noteOn(n).map(_.getData1)

}
