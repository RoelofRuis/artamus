package nl.roelofruis.midi.read

import nl.roelofruis.midi.read.MidiInput.ReadAction
import javax.sound.midi.{MidiMessage, ShortMessage}
import midi.MidiIO

object Midi {

  val IsNoteOn: MidiMessage => Boolean = {
    case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && (msg.getData2 > 0) => true
    case _ => false
  }

  val IsNoteOff: MidiMessage => Boolean = {
    case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_OFF => true
    case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON && (msg.getData2 == 0) => true
    case _ => false
  }

  implicit class MidiInputOps(input: MidiInput) {
    def noteOn(i: Int): MidiIO[List[MidiMessage]] = {
      input.readFrom { list =>
        val isNoteOn = list.headOption.exists(IsNoteOn)
        ReadAction(
          shouldKeep = isNoteOn,
          shouldContinue = ! (isNoteOn && (list.size == i))
        )
      }
    }

    def simultaneousPressedOn: MidiIO[List[MidiMessage]] = {
      input.readFrom { list =>
        ReadAction(
          shouldKeep     = list.headOption.exists(m => IsNoteOn(m) || IsNoteOff(m)),
          shouldContinue = list.isEmpty || (list.count(IsNoteOn) != list.count(IsNoteOff))
        )
      }.map(_.filter(IsNoteOn))
    }
  }

}
