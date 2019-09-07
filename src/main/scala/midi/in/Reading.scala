package midi.in

import midi.util.ReadIntoList
import javax.sound.midi.{MidiMessage, ShortMessage}

object Reading {

  implicit class MidiMessageReads(reader: MidiMessageReader) {

    def noteOn(n: Int): List[MidiMessage] = reader.read(
      ReadIntoList.takeFiltered(n, {
        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => true
        case _ => false
      }
      ))
  }

}
