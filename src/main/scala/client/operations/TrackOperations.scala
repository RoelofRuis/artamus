package client.operations

import client.read.MusicReader.{NoteOn, Simultaneous}
import client.read.{MusicReader, StdIOTools}
import com.google.inject.Inject
import music.math.Rational
import music.primitives._
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
    println(s"Reading time signature...")
    val timeSignature = reader.readTimeSignature

    List(
      CreateMetaSymbol(Position.zero, MetaSymbol.timeSignature(timeSignature)),
      Commit
    )
  })

  registry.registerOperation(OperationToken("key", "track"), () => {
    println(s"Reading key...")
    val root = reader.readSpelledPitch

    println(s"Reading key type...")
    val keyType = reader.readPitchClasses(NoteOn(1)).head match {
      case PitchClass(3) => Scale.MINOR
      case PitchClass(4) => Scale.MAJOR
      case _ => Scale.MAJOR
    }

    List(
      CreateMetaSymbol(Position.zero, MetaSymbol.key(Key(root, keyType))),
      Commit
    )
  })

  registry.registerOperation(OperationToken("note-seq", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val numNotes = StdIOTools.readInt("How many notes?")

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))

    println(s"Reading [$numNotes][$elementDuration] notes...")

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

    println(s"Reading [$numChords][$elementDuration] chords...")

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
