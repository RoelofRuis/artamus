package client.midi.in

import client.midi.util.BlockingQueueReadWrite
import javax.sound.midi.{MidiMessage, Receiver}

class QueuedReceiver(reader: BlockingQueueReadWrite[MidiMessage]) extends Receiver {

  override def send(message: MidiMessage, timeStamp: Long): Unit = reader.write(message)

  override def close(): Unit = ()

}
