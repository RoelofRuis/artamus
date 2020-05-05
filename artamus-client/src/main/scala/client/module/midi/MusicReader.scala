package client.module.midi

import nl.roelofruis.midi.read.MidiInput
import artamus.core.model.primitives._
import javax.sound.midi.ShortMessage
import midi.MidiIO

object MusicReader {

  import nl.roelofruis.midi.read.Midi._
  import artamus.core.ops.transform.analysis.TwelveToneTuning._

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
  }

}
