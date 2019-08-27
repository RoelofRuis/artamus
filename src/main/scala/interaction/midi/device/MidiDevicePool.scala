package interaction.midi.device

import javax.inject.Inject

class MidiDevicePool @Inject() (midiDeviceProvider: MidiDeviceProvider) extends DevicePool {

  override def getInfo: Array[String] = midiDeviceProvider.getInfo

}
