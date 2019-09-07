package client

import client.midi.in.{MidiMessageReader, QueuedMidiMessageReceiver}
import client.midi.out.{SequenceWriter, SimpleSequenceWriter}
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
  def loadReader(deviceHash: DeviceHash): Option[MidiMessageReader] =
    resourceManager.loadTransmitter(deviceHash).map(new QueuedMidiMessageReceiver(_))

  def loadSequenceWriter(deviceHash: DeviceHash): Option[SequenceWriter] = {
    for {
      receiver <- resourceManager.loadReceiver(deviceHash)
      sequencer <- resourceManager.loadSequencer
    } yield new SimpleSequenceWriter(receiver, sequencer)
  }

}
