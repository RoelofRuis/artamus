package client.operations

import client.midi.in.ReadMidiMessage
import client.midi.util.BlockingQueueReader
import com.google.inject.Inject
import javax.sound.midi.{MidiMessage, ShortMessage}
import music._
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: BlockingQueueReader[MidiMessage]
) {

  registry.registerOperation("ts", () => List(SetTimeSignature(TimeSignature.`4/4`)))

  registry.registerOperation("key", () => List(SetKey(Key.`C-Major`)))

  registry.registerOperation("notes", () => {
    reader.read(ReadMidiMessage.noteOn(4))
      .map { case msg: ShortMessage =>
        AddNote(
          Position.apply(Duration.QUARTER, 0),
          Note(Duration.QUARTER, MidiPitch.fromMidiPitchNumber(msg.getData1))
        )
      }
  })

}
