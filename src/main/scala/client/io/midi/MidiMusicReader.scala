package client.io.midi

import client.MusicReader
import client.MusicReader.{NoteOn, ReadMethod, Simultaneous}
import javax.inject.{Inject, Singleton}
import javax.sound.midi.ShortMessage
import midi.MidiIO
import midi.read.MidiInput
import music.primitives._

@Singleton
private[midi] class MidiMusicReader @Inject() (
  reader: MidiInput
) extends MusicReader {

  import midi.read.Midi._
  import music.analysis.TwelveToneTuning._

  def readPitchSpelling: MidiIO[PitchSpelling] = {
    val res = for {
      numbers <- readMidiNoteNumbers(NoteOn(2))
    } yield (numbers, numbers.head.toPc.toStep)

    res match {
      case Left(ex) => Left(ex)
      case Right((_, None)) => readPitchSpelling
      case Right((numbers, Some(step))) =>
        Right(PitchSpelling(step, Accidental(numbers.last.value - numbers.head.value)))
    }
  }

  def readTimeSignatureDivision: MidiIO[TimeSignatureDivision] = {
    val res = for {
      num <- readMidiNoteNumbers(Simultaneous)
      denom <- readMidiNoteNumbers(NoteOn(1))
    } yield TimeSignatureDivision(numberFromBits(num), numberFromBits(denom))

    res match {
      case Left(ex) => Left(ex)
      case Right(None) => readTimeSignatureDivision
      case Right(Some(x)) => Right(x)
    }
  }

  def readPitchClasses(method: ReadMethod): MidiIO[List[PitchClass]] = {
    for {
      notes <- readMidiNoteNumbers(method)
    } yield notes.map(_.toPc)
  }

  def readMidiNoteNumbers(method: ReadMethod): MidiIO[List[MidiNoteNumber]] = {
    val notes = method match {
      case NoteOn(n) => reader.noteOn(n)
      case Simultaneous => reader.simultaneousPressedOn
    }
    notes.map(list => list.map(note => MidiNoteNumber(note.asInstanceOf[ShortMessage].getData1)))
  }

  private def numberFromBits(notes: List[MidiNoteNumber]): Int = {
    notes.foldRight(0){ case (noteNumber, acc) =>
      acc + (1 << noteNumber.toPc.value)
    }
  }
}
