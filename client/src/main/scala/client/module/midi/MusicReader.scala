package client.module.midi

import javax.sound.midi.ShortMessage
import midi.MidiIO
import midi.read.MidiInput
import domain.primitives._

object MusicReader {

  import midi.read.Midi._
  import domain.write.analysis.TwelveToneTuning._

  sealed trait ReadMethod
  case class NoteOn(n: Int) extends ReadMethod
  case object Simultaneous extends ReadMethod

  implicit class InputMusicReader(midiInput: MidiInput) {
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
        case NoteOn(n) => midiInput.noteOn(n)
        case Simultaneous => midiInput.simultaneousPressedOn
      }
      notes.map(list => list.map(note => MidiNoteNumber(note.asInstanceOf[ShortMessage].getData1)))
    }

    private def numberFromBits(notes: List[MidiNoteNumber]): Int = {
      notes.foldRight(0){ case (noteNumber, acc) =>
        acc + (1 << noteNumber.toPc.value)
      }
    }
  }

}