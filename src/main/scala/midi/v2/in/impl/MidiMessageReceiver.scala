package midi.v2.in.impl

import javax.sound.midi.MidiMessage

trait MidiMessageReceiver {

  def receive(message: MidiMessage, timeStamp: Long): Unit

  def closed(): Unit

}
