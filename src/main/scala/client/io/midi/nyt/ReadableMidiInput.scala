package client.io.midi.nyt

import javax.inject.{Inject, Named}
import javax.sound.midi.MidiMessage
import midi.receiver.{AsyncReadableReceiver, MidiInput, ReadAction}
import midi.{DeviceHash, MidiIO, MidiResourceLoader}
import patchpanel.PatchPanel

class ReadableMidiInput @Inject() (
  @Named("midi-in") deviceHash: DeviceHash,
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) extends MidiInput {

  import MidiConnectors.canConnectMidi

  def readFrom(pick: List[MidiMessage] => ReadAction): MidiIO[List[MidiMessage]] = {
    val reader = new AsyncReadableReceiver
    for {
      inputDevice <- loader.loadDevice(deviceHash)
      transmitter <- MidiIO(inputDevice.getTransmitter)
      patchCableId <- MidiIO.wrap(patchPanel.connect(transmitter, reader))
    } yield {
      val result = reader.read(pick)
      patchPanel.disconnect(patchCableId)
      result
    }
  }

}
