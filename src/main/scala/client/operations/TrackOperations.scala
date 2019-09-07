package client.operations

import client.midi.in.MidiMessageReader
import com.google.inject.Inject
import javax.sound.midi.ShortMessage
import music._
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MidiMessageReader
) {

  import client.midi.in.Reading._

  registry.registerOperation("ts", () => List(SetTimeSignature(TimeSignature.`4/4`)))

  registry.registerOperation("key", () => List(SetKey(Key.`C-Major`)))

  registry.registerOperation("notes", () => {
    reader.noteOn(4)
      .map { case msg: ShortMessage =>
        AddNote(
          Position.apply(Duration.QUARTER, 0),
          Note(Duration.QUARTER, MidiPitch.fromMidiPitchNumber(msg.getData1))
        )
      }
  })

}
