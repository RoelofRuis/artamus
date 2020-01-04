package midi.v2

import javax.sound.midi.MidiMessage

trait MidiReader {

  def receive(message: MidiMessage, timeStamp: Long): Unit

  def closed(): Unit

}
