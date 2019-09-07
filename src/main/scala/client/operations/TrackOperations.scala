package client.operations

import client.read.MusicReader
import com.google.inject.Inject
import music._
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  registry.registerOperation("ts", () => {
    List(SetTimeSignature(TimeSignature.`4/4`))
  })

  registry.registerOperation("key", () => {
    List(SetKey(Key(reader.readMusicVector)))
  })

  registry.registerOperation("notes", () => {
    reader
      .readMidiPitch(4)
      .map{ pitch => AddNote(
        Position.apply(Duration.QUARTER, 0),
        Note(Duration.QUARTER, MidiPitch(pitch))
      )}
  })

}
