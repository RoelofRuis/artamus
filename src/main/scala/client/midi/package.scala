package client

import client.midi.in.{MidiMessageReader, SequencerRecordingDevice}
import client.midi.out.SequencePlayer
import client.midi.util.BlockingQueueReader
import javax.sound.midi.{MidiDevice, MidiMessage, MidiSystem}

package object midi {

  type DeviceHash = String

  // TODO: move device management out of here
  private var devices: List[AutoCloseable] = List()
  private def managed(device: AutoCloseable): Unit = devices +:= device
  def close(): Unit = devices.foreach(_.close())

  def loadReader(deviceHash: DeviceHash): Option[BlockingQueueReader[MidiMessage]] =
    loadDevice(deviceHash).map(new MidiMessageReader(_))
      .map { device => managed(device); device } // TODO: rethink!

  def loadPlaybackDevice(deviceHash: DeviceHash): Option[SequencePlayer] =
    loadDevice(deviceHash).map(new SequencePlayer(_))
      .map { device => managed(device); device } // TODO: rethink!

  def loadRecordingDevice(deviceHash: DeviceHash, resolution: Int): Option[SequencerRecordingDevice] =
    loadDevice(deviceHash).map(new SequencerRecordingDevice(_, resolution))
      .map { device => managed(device); device } // TODO: rethink!

  def loadDevice(deviceHash: DeviceHash): Option[MidiDevice] =
    allDescriptions
      .collectFirst { case descr: MidiDeviceDescription if descr.hash == deviceHash => descr.info }
      .map(MidiSystem.getMidiDevice)

  def allDescriptions: Array[MidiDeviceDescription] = MidiSystem.getMidiDeviceInfo.map { MidiDeviceDescription(_) }

}
