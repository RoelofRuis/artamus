package client.operations

import client.read.MusicReader.{NoteOn, Simultaneous}
import client.read.{MusicReader, StdIOTools}
import com.google.inject.Inject
import music.math.Rational
import music.primitives.{Duration, Key, Position, Scale}
import music.symbols.{MetaSymbol, Note}
import server.domain.Commit
import server.domain.track._

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  import music.analysis.TwelveToneEqualTemprament._

  registry.registerOperation(OperationToken("new", "track"), () => {
    List(
      NewTrack,
      Commit
    )
  })

  registry.registerOperation(OperationToken("time-signature", "track"), () => {
    List(
      CreateMetaSymbol(Position.zero, MetaSymbol.timeSignature(reader.readTimeSignature)),
      Commit
    )
  })

  registry.registerOperation(OperationToken("key", "track"), () => {
    List(
      CreateMetaSymbol(Position.zero, MetaSymbol.key(Key(reader.readSpelledPitch, Scale.MAJOR))),
      Commit
    )
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
        val (oct, pc) = tuning.noteNumberToOctAndPc(midiNoteNumber)
        CreateNoteSymbol(
          Position.apply(elementDuration, index),
          Note(
            oct,
            pc,
            elementDuration
          )
        )}

    messages :+ Commit
  })

  registry.registerOperation(OperationToken("chords", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val numChords = StdIOTools.readInt("How many chords?")

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))

    val messages = Range(0, numChords).flatMap { i =>
      reader
        .readMidiNoteNumbers(Simultaneous)
        .map { midiNoteNumber =>
          val (oct, pc) = tuning.noteNumberToOctAndPc(midiNoteNumber)
          CreateNoteSymbol(
            Position.apply(elementDuration, i),
            Note(
              oct,
              pc,
              elementDuration
            )
          )
        }
      }.toList

    messages :+ Commit
  })

}
