package client

import client.midi.in.{MidiMessageReader, QueuedMidiMessageReceiver}
import client.midi.out.{SequenceWriter, SimpleSequenceWriter}
import client.midi.resources.{DefaultMidiResourceManager, MidiResourceManager}

package object midi {

  type DeviceHash = String

  final val resourceManager: MidiResourceManager = new DefaultMidiResourceManager()

  // TODO: clean up even more!
  def loadReader(deviceHash: DeviceHash): Option[MidiMessageReader] =
    resourceManager.loadTransmitter(deviceHash).map(new QueuedMidiMessageReceiver(_))

  def loadSequenceWriter(deviceHash: DeviceHash, resolution: Int): Option[SequenceWriter] = {
    for {
      receiver <- resourceManager.loadReceiver(deviceHash)
      sequencer <- resourceManager.loadSequencer
    } yield new SimpleSequenceWriter(receiver, sequencer, resolution)
  }

}
