package client.operations

import client.MusicReader
import client.MusicReader.{NoteOn, Simultaneous}
import client.io.StdIOTools
import com.google.inject.Inject
import music.math.Rational
import music.primitives._
import music.symbol.{Key, Note, TimeSignature}
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
    val division = reader.readTimeSignatureDivision

    List(
      CreateTimeSignatureSymbol(Position.zero, TimeSignature(division)),
      Commit
    )
  })

  registry.registerOperation(OperationToken("key", "track"), () => {
    println(s"Reading key...")
    val root = reader.readPitchSpelling

    println(s"Reading key type...")
    val keyType = reader.readPitchClasses(NoteOn(1)).head.value match {
      case 3 => Scale.MINOR
      case 4 => Scale.MAJOR
      case _ => Scale.MAJOR
    }

    List(
      CreateKeySymbol(Position.zero, Key(root, keyType)),
      Commit
    )
  })

  registry.registerOperation(OperationToken("notes", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val elementLayout = StdIOTools.read("Element layout?\n. : onset\nr : rest", "Invalid input", {
        val line = scala.io.StdIn.readLine
        if (! line.matches("[\\.r]*")) throw new Exception("incorrect pattern")
        else {
          line.toCharArray.map {
            case '.' => Onset
            case 'r' => Rest
          }
        }
      }
    )

    val elementDuration = Duration(Rational.reciprocal(gridSpacing))
    val numOnsets = elementLayout.count(_ == Onset)

    println(s"Reading [$numOnsets][$elementDuration] grid elements...")

    val messages = elementLayout.zipWithIndex.flatMap {
      case (Onset, i) =>
        reader
          .readMidiNoteNumbers(Simultaneous)
          .map { midiNoteNumber =>
            val (oct, pc) = (midiNoteNumber.toOct, midiNoteNumber.toPc)
            CreateNoteSymbol(
              Position.apply(elementDuration, i),
              Note(
                oct,
                pc,
                elementDuration
              )
            )
          }
      case (Rest, _) => Seq()
    }.toList

    messages :+ Commit
  })

  sealed trait GridElement
  case object Onset extends GridElement
  case object Rest extends GridElement

}
