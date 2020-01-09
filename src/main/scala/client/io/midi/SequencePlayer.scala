package client.io.midi

import javax.inject.{Inject, Named}
import javax.sound.midi.{MetaMessage, Sequence, Sequencer}
import midi.write.MidiSequenceWriter
import midi.{DeviceHash, MidiIO, MidiResourceLoader}
import patching.{PatchCableId, PatchPanel}

class SequencePlayer @Inject() (
  @Named("midi-out") deviceHash: DeviceHash,
  loader: MidiResourceLoader,
  patchPanel: PatchPanel
) extends MidiSequenceWriter {

  private val PATCH_ID = PatchCableId()
  private lazy val loadedSequencer: MidiIO[Sequencer] = loader.loadSequencer()

  import client.io.midi.MidiConnectors._

  override def writeSequence(sequence: Sequence): MidiIO[Unit] = {
    if ( ! patchPanel.hasPatchCable(PATCH_ID)) {
      for {
        outputDevice <- loader.loadDevice(deviceHash)
        receiver <- MidiIO(outputDevice.getReceiver)
        sequencer <- loadedSequencer
        transmitter <- MidiIO(sequencer.getTransmitter)
        _ <- MidiIO.wrap(patchPanel.connect(transmitter, receiver, "Sequence Player", Some(PATCH_ID)))
        _ <- writeSequence(sequencer, sequence)
      } yield ()
    } else {
      for {
        sequencer <- loadedSequencer
        _ <- writeSequence(sequencer, sequence)
      } yield ()
    }
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
