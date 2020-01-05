package midi.v2.out.impl

import javax.annotation.concurrent.NotThreadSafe
import javax.inject.{Inject, Singleton}
import midi.v2.{DeviceHash, MidiIO, MidiResourceLoader}

@NotThreadSafe
@Singleton
class MidiSinkLoader @Inject() (resourceLoader: MidiResourceLoader) {

  private var loadedSinks: Map[DeviceHash, MidiSequencerSink] = Map()

  def loadSequencerSink(hash: DeviceHash): MidiIO[MidiSequencerSink] = loadedSinks.get(hash) match {
    case Some(sink) => Right(sink)
    case None =>
      val res = for {
        device <- resourceLoader.loadDevice(hash)
        sequencer <- resourceLoader.loadSequencer()
        sink <- DefaultMidiSequencerSink.sequenceToDevice(sequencer, device)
      } yield sink

      res match {
        case Left(ex) => Left(ex)
        case Right(sink) =>
          loadedSinks += (hash -> sink)
          Right(sink)
      }
  }

}
