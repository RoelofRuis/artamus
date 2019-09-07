package client.read

import javax.inject.Inject
import midi.in.MidiMessageReader
import music._

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

  def readTimeSignature: TimeSignature = {
    readMidiPitch(2).map{MidiPitch(_).pitchClass} match {
      case num :: denom :: Nil =>
        TimeSignature(num.value + 1, denom.value + 1) match {
          case Some(t) => t
          case _ => readTimeSignature
        }
      case _ => readTimeSignature
    }
  }

  def readMidiPitch(n: Int): List[Int] = reader.noteOn(n).map(_.getData1)

}
