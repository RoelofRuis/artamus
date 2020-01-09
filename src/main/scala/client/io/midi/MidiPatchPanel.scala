package client.io.midi

import javax.inject.Inject
import midi.{DeviceHash, MidiIO, MidiResourceLoader}
import patching.PatchPanel

class MidiPatchPanel @Inject() (
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) {

  import MidiConnectors.canConnectMidi

  def connect(midiIn: DeviceHash, midiOut: DeviceHash): MidiIO[Unit] = {
    for {
      transmitterDevice <- loader.loadDevice(midiIn)
      receiverDevice <- loader.loadDevice(midiOut)
      transmitter <- MidiIO(transmitterDevice.getTransmitter)
      receiver <- MidiIO(receiverDevice.getReceiver)
      _ <- MidiIO.wrap(patchPanel.connect(transmitter, receiver))
    } yield ()
  }

}
