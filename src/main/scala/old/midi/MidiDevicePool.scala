package old.midi

import javax.inject.Inject

class MidiDevicePool @Inject() (midiDeviceProvider: MidiDeviceProvider) {

  def getInfo: Array[String] = midiDeviceProvider.getInfo

}
