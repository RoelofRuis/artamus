package midi.v2.out.impl

import javax.sound.midi.Sequence
import midi.v2.{DeviceHash, MidiIO}
import midi.v2.out.api.MidiOutput

final class SingleDeviceMidiOutput(deviceHash: DeviceHash, loader: MidiSinkLoader) extends MidiOutput {

  override def writeSequence(sequence: Sequence): MidiIO[Unit] = {
    for {
      sink <- loader.loadSequencerSink(deviceHash)
      _ <- sink.writeSequence(sequence)
    } yield ()
  }

}
