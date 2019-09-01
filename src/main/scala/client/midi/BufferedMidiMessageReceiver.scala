package client.midi

import client.util.BlockingReader
import javax.sound.midi.{MidiMessage, Receiver}

import scala.language.higherKinds

class BufferedMidiMessageReceiver(reader: BlockingReader[MidiMessage]) extends Receiver {

  override def send(message: MidiMessage, timeStamp: Long): Unit = reader.write(message)

  override def close(): Unit = ()

}
