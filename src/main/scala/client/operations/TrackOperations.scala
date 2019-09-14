package client.operations

import client.read.{MusicReader, StdIOTools}
import com.google.inject.Inject
import music._
import music.util.math.Rational
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

  registry.registerOperation("note-seq", () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val numNotes = StdIOTools.readInt("How many notes?")

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))

    println(s"Reading [$numNotes][$elementDuration] notes:")

    reader
      .readMidiNoteNumbers(numNotes)
      .zipWithIndex
      .map{ case (midiNoteNumber, index) =>
        AddNote(
          Position.apply(elementDuration, index),
          Note(elementDuration, MidiPitch(midiNoteNumber))
        )}
  })



}
