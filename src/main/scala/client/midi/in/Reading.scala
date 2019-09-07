package client.midi.in

import client.midi.util.BlockingReadList
import javax.sound.midi.{MidiMessage, ShortMessage}

object Reading {

  implicit class MidiMessageReads(reader: MidiMessageReader) {

    def noteOn(n: Int): List[MidiMessage] = reader.read(
      BlockingReadList.takeFiltered(n, {
        case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => true
        case _ => false
      }
      ))
  }

}
