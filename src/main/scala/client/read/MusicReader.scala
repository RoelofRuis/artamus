package client.read

import client.read.MusicReader.{NoteOn, ReadMethod, Simultaneous}
import javax.inject.Inject
import midi.in.MidiMessageReader
import music.symbolic.TimeSignature
import music.symbolic.pitched.{Accidental, MidiNoteNumber, Spelled, Step}

class MusicReader @Inject() (reader: MidiMessageReader) {

  import midi.in.Reading._
  import music.interpret.pitched.TwelveToneEqualTemprament._

  def readSpelledPitch: Spelled = {
    val midiNoteNumbers = readMidiNoteNumbers(NoteOn(2))
    val firstStep = tuning.pc2step(tuning.noteNumberToPitch(midiNoteNumbers.head).p)

    if (firstStep.isEmpty) readSpelledPitch
    else {
      Spelled(Step(firstStep.get.value), Accidental(midiNoteNumbers.last.value - midiNoteNumbers.head.value))
    }
  }

  def readTimeSignature: TimeSignature = {
    readMidiNoteNumbers(NoteOn(2)).map{ tuning.noteNumberToPitch(_).p } match {
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