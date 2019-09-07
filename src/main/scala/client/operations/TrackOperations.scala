package client.operations

import com.google.inject.Inject
import music._
import server.domain.track.{AddNote, SetKey, SetTimeSignature}

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  registry.registerOperation("ts", () => List(SetTimeSignature(TimeSignature.`4/4`)))

  registry.registerOperation("read-mvec", () => {
    println(reader.readMusicVector)

    List()
  })

  registry.registerOperation("key", () => {
    List(SetKey(Key.`C-Major`))
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
