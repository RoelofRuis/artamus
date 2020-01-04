package midi.v2.impl

import java.util.concurrent.CopyOnWriteArraySet

import javax.annotation.concurrent.ThreadSafe
import javax.sound.midi.{MidiDevice, MidiMessage, MidiSystem, Receiver}
import midi.v2.api.{InitializationException, MidiIO}

@ThreadSafe
class ReadableMidiReceiver extends MidiSource with Receiver {

  private val subscribers: CopyOnWriteArraySet[MidiMessageReceiver] = new CopyOnWriteArraySet[MidiMessageReceiver]()

  override def send(message: MidiMessage, timeStamp: Long): Unit = subscribers.forEach(_.receive(message, timeStamp))
  override def close(): Unit = subscribers.forEach(_.closed())

  def connect(receiver: MidiMessageReceiver): Unit = subscribers.add(receiver)
  def disconnect(receiver: MidiMessageReceiver): Unit = subscribers.remove(receiver)

}

object ReadableMidiReceiver {

  def apply(info: MidiDevice.Info): MidiIO[ReadableMidiReceiver] = {
    try {
      val device = MidiSystem.getMidiDevice(info)
      if ( ! device.isOpen) device.open()
      val readable = new ReadableMidiReceiver
      device.getTransmitter.setReceiver(readable)
      Right(readable)
    } catch {
      case ex: Throwable => Left(InitializationException(ex))
    }
  }


}
