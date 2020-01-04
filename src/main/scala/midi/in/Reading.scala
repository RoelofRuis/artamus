package midi.in

import midi.util.ReadIntoList
import javax.sound.midi.{MidiMessage, ShortMessage}

@deprecated
object Reading {

  implicit class MidiMessageReads(reader: MidiMessageReader) {

    val IsNoteOn: MidiMessage => Boolean = {
      case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => true
      case _ => false
    }

    val IsNoteOff: MidiMessage => Boolean = {
      case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_OFF => true
      case _ => false
    }

    def noteOn(n: Int): List[ShortMessage] = reader.read(
      ReadIntoList.takeFiltered(n, msg => IsNoteOn(msg))
    ).asInstanceOf[List[ShortMessage]]

    def simultaneousPressedOn: List[ShortMessage] = reader.read(
      ReadIntoList.takeUntil(
        acc => acc.nonEmpty && acc.count(IsNoteOn) == acc.count(IsNoteOff),
        msg => IsNoteOn(msg) || IsNoteOff(msg)
      )
    ).filter(IsNoteOn).asInstanceOf[List[ShortMessage]]

  }

}
