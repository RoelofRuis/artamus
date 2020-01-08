package midi.read

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import javax.sound.midi.{MidiMessage, Receiver}

import scala.annotation.tailrec

class AsyncReadableReceiver extends Receiver {

  private val queue: BlockingQueue[MidiMessage] = new LinkedBlockingQueue[MidiMessage]()

  override def send(message: MidiMessage, timeStamp: Long): Unit = { queue.offer(message) }

  def read(pick: List[MidiMessage] => ReadAction): List[MidiMessage] = {
    @tailrec
    def loop(acc: List[MidiMessage]): List[MidiMessage] = {
      val newAcc = queue.take() +: acc
      pick(newAcc) match {
        case ReadAction(true, true) => loop(newAcc)
        case ReadAction(true, false) => newAcc
        case ReadAction(false, true) => loop(acc)
        case ReadAction(false, false) => acc
      }
    }
    loop(List())
  }

  override def close(): Unit = {}

}
