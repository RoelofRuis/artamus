package client.midi

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import client.temporal.TemporalReadable
import javax.sound.midi.{MidiMessage, Receiver}

class BufferedMidiMessageReceiver extends Receiver with TemporalReadable[MidiMessage] {

  private var queue: Option[LinkedBlockingQueue[MidiMessage]] = None

  override def startReading(): BlockingQueue[MidiMessage] = {
    val currentQueue = new LinkedBlockingQueue[MidiMessage]()
    queue = Some(currentQueue)
    currentQueue
  }

  override def stopReading(): Unit = queue = None

  override def send(message: MidiMessage, timeStamp: Long): Unit = queue.map(_.offer(message))

  override def close(): Unit = ()

}
