package client.midi.in

import client.midi.util.BlockingQueueReadWrite
import javax.sound.midi.{MidiMessage, Receiver}

import scala.language.higherKinds

class BufferedMidiMessageReceiver(reader: BlockingQueueReadWrite[MidiMessage]) extends Receiver {

  override def send(message: MidiMessage, timeStamp: Long): Unit = reader.write(message)

  override def close(): Unit = ()

}
