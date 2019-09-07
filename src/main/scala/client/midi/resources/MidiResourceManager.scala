package client.midi.resources

import client.midi.{DeviceHash, MidiDeviceDescription}
import javax.sound.midi.{MidiDevice, Receiver, Sequencer, Transmitter}

trait MidiResourceManager {

  def allDescriptions: Array[MidiDeviceDescription]

  def loadSequencer: Option[Sequencer]

  def loadDevice(device: DeviceHash): Option[MidiDevice]

  def loadTransmitter(device: DeviceHash): Option[Transmitter]

  def loadReceiver(deviceHash: DeviceHash): Option[Receiver]

  def closeAll(): Unit

}
