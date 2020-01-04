package midi.v2

import java.util.concurrent.CopyOnWriteArraySet

import javax.annotation.concurrent.ThreadSafe
import javax.sound.midi.{MidiDevice, MidiMessage, MidiSystem, Receiver}

@ThreadSafe
class ReadableMidiReceiver extends MidiReadable with Receiver {

  private val subscribers: CopyOnWriteArraySet[MidiReader] = new CopyOnWriteArraySet[MidiReader]()

  override def send(message: MidiMessage, timeStamp: Long): Unit = subscribers.forEach(_.receive(message, timeStamp))
  override def close(): Unit = subscribers.forEach(_.closed())

  def subscribe(receiver: MidiReader): Unit = subscribers.add(receiver)
  def unsubscribe(receiver: MidiReader): Unit = subscribers.remove(receiver)

}

object ReadableMidiReceiver {

  def apply(info: MidiDevice.Info): Either[MidiException, ReadableMidiReceiver] = {
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
