package interaction.midi.device

import javax.sound.midi.{MidiMessage, Receiver, ShortMessage}

class MidiControlReceiverDecorator(inner: Receiver) extends Receiver {

  override def send(message: MidiMessage, timeStamp: Long): Unit = {
    message match {
      case a: ShortMessage =>
        println(
          s"Channel: ${a.getChannel.toHexString}," +
            s"Command: ${a.getCommand.toHexString}," +
            s"Data1: ${a.getData1.toHexString}," +
            s"Data2: ${a.getData2.toHexString}"
        )
    }

    inner.send(message, timeStamp)
  }

  override def close(): Unit = inner.close()
}
