package client

import client.midi.in.{MidiMessageReader, SequencerRecordingDevice}
import client.midi.out.{SequenceFormatter, SequencePlayer}
import client.midi.util.BlockingQueueReader
import javax.sound.midi.{MidiDevice, MidiMessage, MidiSystem}

package object midi {

  type DeviceHash = String

  def loadReader(deviceHash: DeviceHash): Option[BlockingQueueReader[MidiMessage]] =
    loadDevice(deviceHash).map(new MidiMessageReader(_))

  def loadPlaybackDevice(deviceHash: DeviceHash): Option[SequencePlayer] =
    loadDevice(deviceHash).map(new SequencePlayer(_))

  def loadRecordingDevice(deviceHash: DeviceHash, resolution: Int): Option[SequencerRecordingDevice] =
    loadDevice(deviceHash).map(new SequencerRecordingDevice(_, resolution))

  def loadDevice(deviceHash: DeviceHash): Option[MidiDevice] =
    allDescriptions
      .collectFirst { case descr: MidiDeviceDescription if descr.hash == deviceHash => descr.info }
      .map(MidiSystem.getMidiDevice)

  def allDescriptions: Array[MidiDeviceDescription] = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

}
