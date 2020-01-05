package midi.in.impl

trait MidiSource {

  def connect(receiver: MidiMessageReceiver): Unit

  def disconnect(receiver: MidiMessageReceiver): Unit

}
