package client.midi.in

import client.midi.util.TimedBlockingQueueReader
import javax.sound.midi.{MidiMessage, Receiver}

class QueuedReceiver(reader: TimedBlockingQueueReader[MidiMessage]) extends Receiver {

  override def send(message: MidiMessage, timeStamp: Long): Unit = reader.write(message)

  override def close(): Unit = ()

}
