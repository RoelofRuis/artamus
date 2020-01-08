package midi.in.impl

import java.util.concurrent.CopyOnWriteArraySet

import javax.annotation.concurrent.ThreadSafe
import javax.sound.midi.{MidiDevice, MidiMessage, Receiver}
import midi.MidiIO

@deprecated
@ThreadSafe
class MidiSourceReceiver extends MidiSource with Receiver {

  private val subscribers: CopyOnWriteArraySet[MidiMessageReceiver] = new CopyOnWriteArraySet[MidiMessageReceiver]()

  override def send(message: MidiMessage, timeStamp: Long): Unit = subscribers.forEach(_.receive(message, timeStamp))
  override def close(): Unit = subscribers.forEach(_.closed())

  def connect(receiver: MidiMessageReceiver): Unit = subscribers.add(receiver)
  def disconnect(receiver: MidiMessageReceiver): Unit = subscribers.remove(receiver)

}

object MidiSourceReceiver {

  def fromDevice(device: MidiDevice): MidiIO[MidiSourceReceiver] = {
    val readable = new MidiSourceReceiver
    for {
      _ <- MidiIO(device.getTransmitter.setReceiver(readable))
    } yield readable
  }

}
