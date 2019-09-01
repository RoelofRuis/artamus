package client

import client.midi.MyDevices
import client.util.BlockingQueueReader.BlockingQueueReadMethod
import client.util.BlockingReadList
import javax.sound.midi.{MidiMessage, ShortMessage}

object MidiTestApp extends App {

  val reader = midi.loadReader(MyDevices.iRigUSBMIDI_IN).get

  reader
    .read(noteOnMessages(4))
    .map {
      case msg: ShortMessage => msg.getData1
    }
    .foreach(println)

  reader.close()

  def noteOnMessages(n: Int): BlockingQueueReadMethod[MidiMessage, List] = BlockingReadList.takeFiltered(n, {
    case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => true
    case _ => false
  })
}


