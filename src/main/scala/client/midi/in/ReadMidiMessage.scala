package client.midi.in

import client.midi.util.BlockingQueueReader.BlockingQueueReadMethod
import client.midi.util.BlockingReadList
import javax.sound.midi.{MidiMessage, ShortMessage}

object ReadMidiMessage {

  def noteOn(n: Int): BlockingQueueReadMethod[MidiMessage, List] = BlockingReadList.takeFiltered(n, {
    case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => true
    case _ => false
  })

}
