package interaction.midi.device

import application.api.DevicePool
import javax.inject.Inject

class MidiDevicePool @Inject() (midiDeviceProvider: MidiDeviceProvider) extends DevicePool {

  override def getInfo: Array[String] = midiDeviceProvider.getInfo

}
