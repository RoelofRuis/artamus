package midi.v2.impl

trait MidiSource {

  def connect(receiver: MidiMessageReceiver): Unit

  def disconnect(receiver: MidiMessageReceiver): Unit

}
