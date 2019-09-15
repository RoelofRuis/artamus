package client.operations

import client.read.MusicReader.{NoteOn, Simultaneous}
import client.read.{MusicReader, StdIOTools}
import com.google.inject.Inject
import music.Scale.MajorScale
import music._
import music.util.math.Rational
import server.domain.track.{AddNote, NewTrack, SetKey, SetTimeSignature}

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  registry.registerOperation(OperationToken("new", "track"), () => {
    List(NewTrack)
  })

  registry.registerOperation(OperationToken("time-signature", "track"), () => {
    List(SetTimeSignature(reader.readTimeSignature))
  })

  registry.registerOperation(OperationToken("key", "track"), () => {
    List(SetKey(Key(reader.readMusicVector, MajorScale)))
  })

  registry.registerOperation(OperationToken("note-seq", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val numNotes = StdIOTools.readInt("How many notes?")

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))

    println(s"Reading [$numNotes][$elementDuration] notes:")

    reader
      .readMidiNoteNumbers(NoteOn(numNotes))
      .zipWithIndex
      .map{ case (midiNoteNumber, index) =>
        AddNote(
          Position.apply(elementDuration, index),
          Note(elementDuration, MidiPitch(midiNoteNumber))
        )}
  })

  registry.registerOperation(OperationToken("chord", "track"), () => {
    reader
      .readMidiNoteNumbers(Simultaneous)
      .map { midiNoteNumber =>
        AddNote(
          Position.zero,
          Note(Duration.QUARTER, MidiPitch(midiNoteNumber))
        )
      }
  })



}
