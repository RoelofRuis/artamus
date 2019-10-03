package client.read

import client.read.MusicReader.{NoteOn, ReadMethod, Simultaneous}
import javax.inject.Inject
import midi.in.MidiMessageReader
import music.symbolic.pitch.{Accidental, MidiNoteNumber, SpelledPitch, Step}
import music.symbolic.symbol.TimeSignature

class MusicReader @Inject() (reader: MidiMessageReader) {

  import midi.in.Reading._
  import music.interpret.pitched.TwelveToneEqualTemprament._

  def readSpelledPitch: SpelledPitch = {
    val midiNoteNumbers = readMidiNoteNumbers(NoteOn(2))
    val firstStep = tuning.pc2step(tuning.noteNumberToPitch(midiNoteNumbers.head).pitchClass)

    if (firstStep.isEmpty) readSpelledPitch
    else {
      SpelledPitch(Step(firstStep.get.value), Accidental(midiNoteNumbers.last.value - midiNoteNumbers.head.value))
    }
  }

  def readTimeSignature: TimeSignature = {
    val num = numberFromBits(readMidiNoteNumbers(Simultaneous))
    val denom = numberFromBits(readMidiNoteNumbers(NoteOn(1)))

    TimeSignature(num, denom) match {
      case Some(t) => t
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

  def numberFromBits(notes: List[MidiNoteNumber]): Int = {
    notes.foldRight(0){ case (noteNumber, acc) =>
      acc + (1 << tuning.noteNumberToPitch(noteNumber).pitchClass.value)
    }
  }

}

object MusicReader {

  sealed trait ReadMethod
  case class NoteOn(n: Int) extends ReadMethod
  case object Simultaneous extends ReadMethod

}