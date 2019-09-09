package client.read

import javax.inject.Inject
import midi.in.MidiMessageReader
import music._

class MusicReader @Inject() (reader: MidiMessageReader) {

  import midi.in.Reading._

  def readMusicVector: MusicVector = {
    val midiNoteNumbers = readMidiNoteNumbers(2)
    val firstStep = Scale.MAJOR_SCALE_MATH.pitchClassToStep(MidiPitch(midiNoteNumbers.head).pitchClass)

    if (firstStep.isEmpty) readMusicVector
    else {
      MusicVector(firstStep.get, midiNoteNumbers.last diff midiNoteNumbers.head)
    }
  }

  def readTimeSignature: TimeSignature = {
    readMidiNoteNumbers(2).map{ MidiPitch(_).pitchClass } match {
      case num :: denom :: Nil =>
        TimeSignature(num.value + 1, denom.value + 1) match {
          case Some(t) => t
          case _ => readTimeSignature
        }
      case _ => readTimeSignature
    }
  }

  def readMidiNoteNumbers(n: Int): List[MidiNoteNumber] = reader.noteOn(n).map(s => MidiNoteNumber(s.getData1))

}
