package client.io.midi

import client.MusicReader
import client.MusicReader.{NoteOn, ReadMethod, Simultaneous}
import javax.inject.Inject
import midi.in.MidiMessageReader
import music.primitives._
import music.spelling.SpelledPitch

private[midi] class MidiMusicReader @Inject() (reader: MidiMessageReader) extends MusicReader {

  import midi.in.Reading._
  import music.analysis.TwelveToneEqualTemprament._

  def readSpelledPitch: SpelledPitch = {
    val midiNoteNumbers = readMidiNoteNumbers(NoteOn(2))
    val firstStep = tuning.pcToStep(tuning.noteNumberToPc(midiNoteNumbers.head))

    if (firstStep.isEmpty) readSpelledPitch
    else {
      SpelledPitch(Step(firstStep.get.value), Accidental(midiNoteNumbers.last.value - midiNoteNumbers.head.value))
    }
  }

  def readTimeSignatureDivision: TimeSignatureDivision = {
    val num = numberFromBits(readMidiNoteNumbers(Simultaneous))
    val denom = numberFromBits(readMidiNoteNumbers(NoteOn(1)))

    TimeSignatureDivision(num, denom) match {
      case Some(t) => t
      case _ => readTimeSignatureDivision
    }
  }

  def readPitchClasses(method: ReadMethod): List[PitchClass] = {
    readMidiNoteNumbers(method).map(tuning.noteNumberToPc)
  }

  def readMidiNoteNumbers(method: ReadMethod): List[MidiNoteNumber] = {
    val notes = method match {
      case NoteOn(n) => reader.noteOn(n)
      case Simultaneous => reader.simultaneousPressedOn
    }
    notes.map(s => MidiNoteNumber(s.getData1))
  }

  private def numberFromBits(notes: List[MidiNoteNumber]): Int = {
    notes.foldRight(0){ case (noteNumber, acc) =>
      acc + (1 << tuning.noteNumberToPc(noteNumber).value)
    }
  }
}
