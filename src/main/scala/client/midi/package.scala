package client

import client.midi.in.{MidiMessageReader, SequencerRecordingDevice}
import client.midi.out.PlaybackDevice
import client.util.BlockingQueueReader
import javax.sound.midi.{MidiDevice, MidiMessage, MidiSystem}

package object midi {

  final val TICKS_PER_QUARTER: Int = 4 // TODO: move this to config

  type DeviceHash = String

  def loadReader(deviceHash: DeviceHash): Option[BlockingQueueReader[MidiMessage]] =
    loadDevice(deviceHash).map(new MidiMessageReader(_))

  def loadPlaybackDevice(deviceHash: DeviceHash): Option[PlaybackDevice] =
    loadDevice(deviceHash).map(new PlaybackDevice(_, TICKS_PER_QUARTER))

  def loadRecordingDevice(deviceHash: DeviceHash): Option[SequencerRecordingDevice] =
    loadDevice(deviceHash).map(new SequencerRecordingDevice(_, TICKS_PER_QUARTER))

  def loadDevice(deviceHash: DeviceHash): Option[MidiDevice] =
    allDescriptions
      .collectFirst { case descr: MidiDeviceDescription if descr.hash == deviceHash => descr.info }
      .map(MidiSystem.getMidiDevice)

  def allDescriptions: Array[MidiDeviceDescription] = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

  object MyDevices {
    // TODO: move this to config
    val GervillSoftSynt: DeviceHash = "55c8a757"
    val FocusriteUSBMIDI_IN: DeviceHash = "658ef990"
    val FocusriteUSBMIDI_OUT: DeviceHash = "c7797746"
    val iRigUSBMIDI_IN: DeviceHash = "e98b95f2"

  }

}
