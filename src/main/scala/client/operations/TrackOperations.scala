package client.operations

import client.read.MusicReader.{NoteOn, Simultaneous}
import client.read.{MusicReader, StdIOTools}
import com.google.inject.Inject
import music.math.Rational
import music.symbolic._
import music.symbolic.const.Scales
import server.control.PublishChanges
import server.domain.track.{AddNote, NewTrack, SetKey, SetTimeSignature}

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  import music.interpret.pitched.TwelveToneEqualTemprament._

  registry.registerOperation(OperationToken("new", "track"), () => {
    List(NewTrack)
  })

  registry.registerOperation(OperationToken("time-signature", "track"), () => {
    List(SetTimeSignature(reader.readTimeSignature))
  })

  registry.registerOperation(OperationToken("key", "track"), () => {
    List(SetKey(Key(reader.readSpelledPitch, Scales.MAJOR)))
  })

  registry.registerOperation(OperationToken("note-seq", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val numNotes = StdIOTools.readInt("How many notes?")

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))

    println(s"Reading [$numNotes][$elementDuration] notes:")

    val messages = reader
      .readMidiNoteNumbers(NoteOn(numNotes))
      .zipWithIndex
      .map{ case (midiNoteNumber, index) =>
        AddNote(
          Position.apply(elementDuration, index),
          Note(elementDuration, tuning.noteNumberToPitch(midiNoteNumber))
        )}

    messages :+ PublishChanges
  })

  registry.registerOperation(OperationToken("chords", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val numChords = StdIOTools.readInt("How many chords?")

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))

    val messages = Range(0, numChords).flatMap { i =>
      reader
        .readMidiNoteNumbers(Simultaneous)
        .map { midiNoteNumber =>
          AddNote(
            Position.apply(elementDuration, i),
            Note(elementDuration, tuning.noteNumberToPitch(midiNoteNumber))
          )
        }
      }.toList

    messages :+ PublishChanges
  })

}
