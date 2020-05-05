package client.module.midi

import client.midi.MidiResourceLoader
import client.midi.read.MidiInput.ReadAction
import client.midi.read.{AsyncReadableReceiver, MidiInput}
import javax.inject.{Inject, Named}
import javax.sound.midi.MidiMessage
import midi.{DeviceHash, MidiIO}
import client.patching.PatchPanel

class ReadableMidiInput @Inject() (
  @Named("client.midi-in") deviceHash: DeviceHash,
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) extends MidiInput {

  import client.module.midi.MidiConnectors.canConnectMidi

  def readFrom(pick: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]] = {
    val reader = new AsyncReadableReceiver
    for {
      inputDevice <- loader.loadDevice(deviceHash)
      transmitter <- MidiIO(inputDevice.getTransmitter)
      patchCableId <- MidiIO.wrap(patchPanel.connect(transmitter, reader, "ReadableMidiInput"))
    } yield {
      val result = reader.read(pick)
      patchPanel.disconnect(patchCableId)
      result
    }
  }

}
