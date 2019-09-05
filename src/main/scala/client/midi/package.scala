package client

import client.midi.in.MidiMessageReader
import client.midi.out.SequencePlayer
import client.midi.util.BlockingQueueReader
import javax.sound.midi._

package object midi {

  type DeviceHash = String

  val resourceManager: MidiResourceManager = new DefaultMidiResourceManager()

  trait MidiResourceManager {

    def allDescriptions: Array[MidiDeviceDescription]

    def loadSequencer: Option[Sequencer]

    def loadDevice(device: DeviceHash): Option[MidiDevice]

    def loadTransmitter(device: DeviceHash): Option[Transmitter]

    def loadReceiver(deviceHash: DeviceHash): Option[Receiver]

    def closeAll(): Unit

  }

  // TODO: clean up even more!
  def loadReader(deviceHash: DeviceHash): Option[BlockingQueueReader[MidiMessage]] =
    resourceManager.loadTransmitter(deviceHash).map(new MidiMessageReader(_))

  def loadPlaybackDevice(deviceHash: DeviceHash): Option[SequencePlayer] = {
    for {
      receiver <- resourceManager.loadReceiver(deviceHash)
      sequencer <- resourceManager.loadSequencer
    } yield new SequencePlayer(receiver, sequencer)
  }

}
