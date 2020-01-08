package client.io.midi

import javax.inject.{Inject, Named}
import javax.sound.midi.{MetaMessage, Sequence, Sequencer}
import midi.write.MidiOutput
import midi.{DeviceHash, MidiIO, MidiResourceLoader}
import patchpanel.PatchPanel

class SequenceMidiOutput @Inject() (
  @Named("midi-out") deviceHash: DeviceHash,
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) extends MidiOutput {

  import client.io.midi.MidiConnectors._

  // TODO: do not connect every time!
  override def writeSequence(sequence: Sequence): MidiIO[Unit] = {
    for {
      outputDevice <- loader.loadDevice(deviceHash)
      receiver <- MidiIO(outputDevice.getReceiver)
      sequencer <- loader.loadSequencer()
      transmitter <- MidiIO(sequencer.getTransmitter)
      _ <- MidiIO.wrap(patchPanel.connect(transmitter, receiver))
      _ <- writeSequence(sequencer, sequence)
    } yield ()
  }

  private val END_OF_TRACK = 47

  private def writeSequence(sequencer: Sequencer, sequence: Sequence): MidiIO[Unit] = {
    sequencer.addMetaEventListener((meta: MetaMessage) => {
      if (meta.getType == END_OF_TRACK) { sequencer.stop() }
    })

    MidiIO {
      if (sequencer.isRunning) sequencer.stop()
      sequencer.setSequence(sequence)
      sequencer.setTickPosition(0)
      sequencer.setTempoInBPM(120)
      sequencer.start()
    }
  }

}
