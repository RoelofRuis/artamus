package midi.v2

trait MidiReadable {

  def subscribe(receiver: MidiReader): Unit
  def unsubscribe(receiver: MidiReader): Unit

}
