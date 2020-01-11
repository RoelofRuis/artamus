package midi.read

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import javax.sound.midi.{MidiMessage, Receiver}

final class ThreadCallbackReceiver private (
  callback: (MidiMessage, Long) => Unit
) extends Thread with Receiver {

  private val queue: BlockingQueue[(MidiMessage, Long)] = new LinkedBlockingQueue[(MidiMessage, Long)]()

  override def send(message: MidiMessage, timeStamp: Long): Unit = {
    val elem = (message, timeStamp)
    queue.offer(elem)
  }

  override def run(): Unit = {
    try {
      while ( ! isInterrupted ) {
        val elem = queue.take()
        callback(elem._1, elem._2)
      }
    } catch {
      case _: InterruptedException =>
    }
  }

  override def close(): Unit = this.interrupt()

}

object ThreadCallbackReceiver {

  def apply(callback: (MidiMessage, Long) => Unit): ThreadCallbackReceiver = {
    val receiver = new ThreadCallbackReceiver(callback)
    receiver.start()
    receiver
  }

}