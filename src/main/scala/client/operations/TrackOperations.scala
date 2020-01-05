package client.operations

import client.MusicReader
import client.MusicReader.{NoteOn, Simultaneous}
import client.io.StdIOTools
import com.google.inject.Inject
import midi.v2.MidiIO
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.primitives.{Note, NoteGroup, TimeSignature, _}
import protocol.Command
import server.actions.writing._

import scala.annotation.tailrec

class TrackOperations @Inject() (
  registry: OperationRegistry,
  reader: MusicReader
) {

  import music.analysis.TwelveToneTuning._

  registry.registerOperation(OperationToken("analyse", "track"), () => {
    Operation.list(Analyse, Render)
  })

  registry.registerOperation(OperationToken("new", "workspace"), () => {
    Operation.list(NewWorkspace, Render)
  })

  registry.registerOperation(OperationToken("time-signature", "track"), () => {
    println(s"Reading time signature...")
    val res = for {
      division <- reader.readTimeSignatureDivision
    } yield List(WriteTimeSignature(Position.ZERO, TimeSignature(division)), Render)

    res.toTry
  })

  registry.registerOperation(OperationToken("key", "track"), () => {
    println(s"Reading key...")
    val res = for {
      root <- reader.readPitchSpelling
      _ = println(s"Reading key type...")
      keyType <- reader.readPitchClasses(NoteOn(1))
    } yield {
      val scale = keyType.head.value match {
        case 3 => Scale.MINOR
        case 4 => Scale.MAJOR
        case _ => Scale.MAJOR
      }
      List(
        WriteKey(Position.ZERO, Key(root, scale)),
        Render
      )
    }

    res.toTry
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
    def read(elements: List[GridElement], commands: List[Command] = List(), currentPos: Int = 0): MidiIO[List[Command]] = {
      elements match {
        case Nil => Right(commands)

        case Onset :: tail =>
          val elementDuration = baseDuration * (1 + tail.takeWhile(_ == Continued).size)
          tail.dropWhile(_ == Continued)
          val notes = for {
            x <- reader.readMidiNoteNumbers(Simultaneous)
          } yield x.map { midiNoteNumber => Note(midiNoteNumber.toOct, midiNoteNumber.toPc) }

          notes match {
            case Left(ex) => Left(ex)
            case Right(noteList) =>
              val newCommand = WriteNoteGroup(NoteGroup(Window(Position.at(baseDuration * currentPos), elementDuration), noteList))
              read(elements.tail, commands :+ newCommand, currentPos + 1)
          }

        case Rest :: _ => read(elements.tail, commands, currentPos + 1)

        case Continued :: _ => read(elements.tail, commands, currentPos + 1)
      }
    }

    read(elementLayout).map(_ :+ Render).toTry
  })

  sealed trait GridElement
  case object Onset extends GridElement
  case object Continued extends GridElement
  case object Rest extends GridElement

}
