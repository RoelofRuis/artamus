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
    List(SetTimeSignature(reader.readTimeSignature))
  })

  registry.registerOperation("key", () => {
    List(SetKey(Key(reader.readMusicVector)))
  })

  registry.registerOperation("notes", () => {
    def getInt: Int = {
      println("how many?")
       try { scala.io.StdIn.readInt() }
       catch { case _: NumberFormatException => getInt }
    }

    val numNotes = getInt

    println(s"reading $numNotes notes:")

    reader
      .readMidiNoteNumbers(numNotes)
      .map{ midiNoteNumber =>
        AddNote(
          Position.apply(Duration.QUARTER, 0),
          Note(Duration.QUARTER, MidiPitch(midiNoteNumber))
        )}
  })

}
