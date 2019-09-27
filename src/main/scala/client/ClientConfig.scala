package client

import midi.DeviceHash

trait ClientConfig {

  private object MyDevices {
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"
  }

  val port = 9999

  val ticksPerQuarter: Int = 4
  val midiOut: DeviceHash = MyDevices.FocusriteUSBMIDI_OUT
  val midiIn: DeviceHash = MyDevices.iRigUSBMIDI_IN

}
