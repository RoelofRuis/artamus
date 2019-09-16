package client.read

import client.read.MusicReader.{NoteOn, ReadMethod, Simultaneous}
import javax.inject.Inject
import midi.in.MidiMessageReader
import music.math.ScaleMath
import music.symbolic.{MidiNoteNumber, MidiPitch, MusicVector, TimeSignature}

class MusicReader @Inject() (reader: MidiMessageReader) {

  import midi.in.Reading._

  def readMusicVector: MusicVector = {
    val midiNoteNumbers = readMidiNoteNumbers(NoteOn(2))
    val firstStep = ScaleMath.ON_A_PIANO.pitchClassToStep(MidiPitch(midiNoteNumbers.head).pitchClass)

    if (firstStep.isEmpty) readMusicVector
    else {
      MusicVector(firstStep.get, midiNoteNumbers.head diff midiNoteNumbers.last)
    }
  }

  def readTimeSignature: TimeSignature = {
    readMidiNoteNumbers(NoteOn(2)).map{ MidiPitch(_).pitchClass } match {
      case num :: denom :: Nil =>
        TimeSignature(num.value + 1, denom.value + 1) match {
          case Some(t) => t
          case _ => readTimeSignature
        }
      case _ => readTimeSignature
    }
  }

  def readMidiNoteNumbers(method: ReadMethod): List[MidiNoteNumber] = {
    val notes = method match {
      case NoteOn(n) => reader.noteOn(n)
      case Simultaneous => reader.simultaneousPressedOn
    }
    notes.map(s => MidiNoteNumber(s.getData1))
  }

}

object MusicReader {

  sealed trait ReadMethod
  case class NoteOn(n: Int) extends ReadMethod
  case object Simultaneous extends ReadMethod

}