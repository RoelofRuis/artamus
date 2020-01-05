package midi.out.impl

import javax.sound.midi.Sequence
import midi.out.api.MidiOutput
import midi.{DeviceHash, MidiIO}

final class SingleDeviceMidiOutput(deviceHash: DeviceHash, loader: MidiSinkLoader) extends MidiOutput {

  override def writeSequence(sequence: Sequence): MidiIO[Unit] = {
    for {
      sink <- loader.loadSequencerSink(deviceHash)
      _ <- sink.writeSequence(sequence)
    } yield ()
  }

}
