package midi.v2.impl

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import javax.sound.midi.MidiMessage
import midi.v2.api.ReadAction

import scala.annotation.tailrec

class QueuedMidiReader extends MidiMessageReceiver with MidiReader {

  private val queue: BlockingQueue[MidiMessage] = new LinkedBlockingQueue[MidiMessage]()

  override def receive(message: MidiMessage, timeStamp: Long): Unit = { queue.offer(message) }

  override def closed(): Unit = {}

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

}
