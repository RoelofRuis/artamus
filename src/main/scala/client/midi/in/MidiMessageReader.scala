package client.midi.in

import client.midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod
import javax.sound.midi.MidiMessage

import scala.language.higherKinds

trait MidiMessageReader {

  def read[L[_]](readMethod: BlockingQueueReadMethod[MidiMessage, L]): L[MidiMessage]

}
