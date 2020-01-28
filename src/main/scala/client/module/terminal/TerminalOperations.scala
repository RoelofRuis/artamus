package client.module.terminal

import client.util.{StdIOTools, ClientLogging}
import client.module.Operations.{OperationRegistry, ServerOperation}
import javax.inject.Inject
import music.math.Rational
import music.math.temporal.{Duration, Position, Window}
import music.primitives.{NoteGroup, TimeSignature}
import protocol.Command
import protocol.client.api.ClientInterface
import server.actions.writing.{Perform, Render, WriteKey, WriteNoteGroup, WriteTimeSignature}

import scala.annotation.tailrec

class TerminalOperations @Inject() (
  registry: OperationRegistry,
  client: ClientInterface,
) {

  import ClientLogging._

  registry.local("print-notes", "query (terminal)", {
    client.sendQueryLogged(Perform) match {
      case Right(track) =>
        println("Playing:")
        track.notes.foreach { note => println(note) }
      case _ =>
    }
  })

  registry.server("time-signature", "edit (terminal)", {
    println(s"Reading time signature...")
    ServerOperation(WriteTimeSignature(Position.ZERO, TimeSignature(TerminalReader.readTimeSignatureDivision)), Render)
  })

  registry.server("key", "edit (terminal)", {
    println(s"Reading key...")
    ServerOperation(WriteKey(Position.ZERO, TerminalReader.readKey), Render)
  })

  registry.server("write", "edit (terminal)", {
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
    })

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
          val notes = TerminalReader.readNotes
          val newCommand = WriteNoteGroup(
            NoteGroup(Window(Position.at(baseDuration * currentPos), elementDuration), notes)
          )

          read(elements.tail, commands :+ newCommand, currentPos + 1)

        case Rest :: _ => read(elements.tail, commands, currentPos + 1)

        case Continued :: _ => read(elements.tail, commands, currentPos + 1)
      }
    }

    val ops = read(elementLayout) :+ Render
    ServerOperation(ops: _*)
  })

  sealed trait GridElement
  case object Onset extends GridElement
  case object Continued extends GridElement
  case object Rest extends GridElement

}
