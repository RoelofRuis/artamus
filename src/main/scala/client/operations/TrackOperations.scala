package client.operations

import client.MusicReader
import client.MusicReader.{NoteOn, Simultaneous}
import client.io.StdIOTools
import com.google.inject.Inject
import music.domain.track.symbol.Note
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.primitives.{TimeSignature, _}
import protocol.Command
import server.domain.Analyse
import server.domain.track._

import scala.annotation.tailrec

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  import music.analysis.TwelveToneTuning._

  registry.registerOperation(OperationToken("new", "workspace"), () => {
    List(
      NewWorkspace,
      Analyse
    )
  })

  registry.registerOperation(OperationToken("time-signature", "track"), () => {
    println(s"Reading time signature...")
    val division = reader.readTimeSignatureDivision

    List(
      WriteTimeSignature(Position.ZERO, TimeSignature(division)),
      Analyse
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
      WriteKey(Position.ZERO, Key(root, keyType)),
      Analyse
    )
  })

  registry.registerOperation(OperationToken("notes", "track"), () => {
    val gridSpacing = StdIOTools.readInt("Grid spacing of 1/_?")
    val elementLayout = StdIOTools.read("Element layout?\n. : onset\n- : continuation\nr : rest", "Invalid input", {
        val line = scala.io.StdIn.readLine
        if (! line.matches("[\\.\\-r]*")) throw new Exception("incorrect pattern")
        else {
          line.toCharArray.map {
            case '.' => Onset
            case '-' => Continued
            case 'r' => Rest
          }.toList
        }
      }
    )

    val baseDuration = Duration(Rational.reciprocal(gridSpacing))
    val numOnsets = elementLayout.count(_ == Onset)

    println(s"Reading [$numOnsets][$baseDuration] grid elements...")

    @tailrec
    def read(elements: List[GridElement], commands: List[Command] = List(), currentPos: Int = 0): List[Command] = {
      elements match {
        case Nil => commands
        case Onset :: tail =>
          val elementDuration = baseDuration * (1 + tail.takeWhile(_ == Continued).size)
          tail.dropWhile(_ == Continued)
          val newCommands = reader
            .readMidiNoteNumbers(Simultaneous)
            .map { midiNoteNumber =>
              val (oct, pc) = (midiNoteNumber.toOct, midiNoteNumber.toPc)
              WriteNote(Window(Position.at(baseDuration * currentPos), elementDuration), Note(oct, pc))
            }
          read(elements.tail, commands ++ newCommands, currentPos + 1)
        case Rest :: _ => read(elements.tail, commands, currentPos + 1)
        case Continued :: _ => read(elements.tail, commands, currentPos + 1)
      }
    }

    read(elementLayout) :+ Analyse
  })

  sealed trait GridElement
  case object Onset extends GridElement
  case object Continued extends GridElement
  case object Rest extends GridElement

}
