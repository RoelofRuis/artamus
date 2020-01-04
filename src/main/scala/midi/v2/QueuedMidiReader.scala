package midi.v2

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import javax.sound.midi.MidiMessage

import scala.annotation.tailrec

class QueuedMidiReader extends MidiReader {

  private val queue: BlockingQueue[MidiMessage] = new LinkedBlockingQueue[MidiMessage]()

  override def receive(message: MidiMessage, timeStamp: Long): Unit = { queue.offer(message) }

  override def closed(): Unit = {}



  def readFromQueue(pick: List[MidiMessage] => ReadAction): List[MidiMessage] = {
    @tailrec
    def loop(acc: List[MidiMessage]): List[MidiMessage] = {
      val newAcc = acc :+ queue.take()
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

// Interface omheen vouwen en hogere orde uitdrukken op dit object:
// Wraps MidiReadable
//val IsNoteOn: MidiMessage => Boolean = {
//  case msg: ShortMessage if msg.getCommand == ShortMessage.NOTE_ON => true
//  case _ => false
//}
//
//  def noteOn(i: Int): Either[MidiException, List[MidiMessage]] = {
//  readFrom { list =>
//  val isNoteOn = list.headOption.exists(IsNoteOn)
//  val shouldStop = isNoteOn && (list.size == i)
//  ReadAction(isNoteOn, shouldStop)
//}
//}