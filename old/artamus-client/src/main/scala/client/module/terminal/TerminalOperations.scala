package client.module.terminal

import client.infra.{Client, ClientInteraction}
import client.module.Operations.{OperationRegistry, ServerOperation}
import client.module.StdIOTools
import artamus.core.api.Command
import artamus.core.api.Control.Commit
import artamus.core.api.Display.Render
import artamus.core.api.Perform.PreparePerformance
import artamus.core.api.Write._
import artamus.core.math.Rational
import artamus.core.math.temporal.{Duration, Position, Window}
import artamus.core.model.primitives.NoteGroup
import javax.inject.Inject

import scala.annotation.tailrec

class TerminalOperations @Inject() (
  registry: OperationRegistry,
  client: Client,
) {

  import ClientInteraction._

  registry.server("commit", "control", {
    ServerOperation(Commit())
  })

  registry.local("print-notes", "query (terminal)", {
    client.sendQuery(PreparePerformance) match {
      case Right(track) =>
        println("Playing:")
        track.notes.foreach { note => println(note) }
      case _ =>
    }
  })

  registry.server("time-signature", "edit (terminal)", {
    println(s"Reading time signature...")
    ServerOperation(WriteMetre(Position.ZERO, TerminalReader.readMetre), Render)
  })

  registry.server("key", "edit (terminal)", {
    println(s"Reading key...")
    ServerOperation(WriteKey(Position.ZERO, TerminalReader.readKey), Render)
  })

  registry.local("layers", "query (terminal)", {
    client.sendQuery(GetLayers) match {
      case Right(layers) =>
        println("Layers")
        layers.foreach { case (index, (id, isVisible)) =>
          println(s"$index ($id)\nvisible: $isVisible")
        }
      case _ =>
    }
  })

  registry.server("hide", "edit", {
    client.sendQuery(GetLayers)
      .flatMap { layers =>
        layers.foreach(index => println(s"$index."))
        val layer = StdIOTools.readInt("Pick a layer")
        layers.get(layer) match {
          case Some((layerId, _)) => Right(layerId)
          case None => Left(())
        }
      }
      .fold(
        _ => ServerOperation(),
        id => ServerOperation(SetLayerVisibility(id, isVisible = false), Render)
      )
  })

  registry.server("show", "edit", {
    client.sendQuery(GetLayers)
      .flatMap { layers =>
        layers.foreach(index => println(s"$index"))
        val layer = StdIOTools.readInt("Pick a layer")
        layers.get(layer) match {
          case Some((layerId, _)) => Right(layerId)
          case None => Left(())
        }
      }
      .fold(
        _ => ServerOperation(),
        id => ServerOperation(SetLayerVisibility(id, isVisible = true), Render)
      )
  })

  registry.server("delete-layer", "edit", {
    client.sendQuery(GetLayers)
      .flatMap { layers =>
        layers.foreach(index => println(s"$index"))
        val layer = StdIOTools.readInt("Pick a layer")
        layers.get(layer) match {
          case Some((layerId, _)) => Right(layerId)
          case None => Left(())
        }
      }
      .fold(
        _ => ServerOperation(),
        id => ServerOperation(DeleteLayer(id), Render)
      )
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
