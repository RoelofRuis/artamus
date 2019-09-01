package client

import javax.sound.midi.{MidiDevice, MidiSystem}

package object midi {

  type DeviceHash = String

  def loadTransmitter(deviceHash: DeviceHash): Option[TransmittingDevice] =
    loadDevice(deviceHash).map(new TransmittingDevice(_))

  def loadDevice(deviceHash: DeviceHash): Option[MidiDevice] =
    allDescriptions
      .collectFirst { case descr: MidiDeviceDescription if descr.hash == deviceHash => descr.info }
      .map(MidiSystem.getMidiDevice)

  def allDescriptions: Array[MidiDeviceDescription] = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

  object MyDevices {
    // TODO: later move this to config
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"

  }

}
