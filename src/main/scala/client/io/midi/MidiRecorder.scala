package client.io.midi

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import javax.sound.midi.{MidiMessage, Receiver}

class MidiRecorder extends Thread with Receiver {

  private val queue: BlockingQueue[(MidiMessage, Long)] = new LinkedBlockingQueue[(MidiMessage, Long)]()

  override def run(): Unit = {
    try {
      while ( ! isInterrupted ) {
        val elem = queue.take()
        println(s"Recorded [$elem]")
      }
    } catch {
      case _: InterruptedException =>
    }
  }

  override def send(message: MidiMessage, timeStamp: Long): Unit = {
    val elem = (message, timeStamp)
    queue.offer(elem)
  }

  override def close(): Unit = interrupt()

}
