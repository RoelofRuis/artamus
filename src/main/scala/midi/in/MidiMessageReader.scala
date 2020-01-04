package midi.in

import midi.util.TemporalReadableBlockingQueue.BlockingQueueReadMethod
import javax.sound.midi.MidiMessage

import scala.language.higherKinds

@deprecated
trait MidiMessageReader {

  def read[L[_]](readMethod: BlockingQueueReadMethod[MidiMessage, L]): L[MidiMessage]

}
