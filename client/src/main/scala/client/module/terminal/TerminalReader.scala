package client.module.terminal

import client.util.StdIOTools
import domain.primitives._

import scala.annotation.tailrec

object TerminalReader {

  import domain.write.analysis.TwelveToneTuning._

  @tailrec
  def readTimeSignatureDivision: TimeSignatureDivision = {
    val division = StdIOTools.read[(Int, Int)]("Input time signature: x/y", "Invalid value", {
      val elems = scala.io.StdIn.readLine.split("/")
      (elems(0).toInt, elems(1).toInt)
    })

    TimeSignatureDivision(division._1, division._2) match {
      case Some(ts) => ts
      case None =>
        println("Invalid time signature")
        readTimeSignatureDivision
    }
  }

  @tailrec
  def readKey: Key = {
    val (root, tpe) = StdIOTools.read[(PitchSpelling, String)]("Input key ('step' 'acc' 'type')", "Invalid key type", {
      val elems = scala.io.StdIn.readLine.split(" ")
      val ps = PitchSpelling(Step(elems(0).toInt), Accidental(elems(1).toInt))
      (ps, elems(2).toLowerCase)
    })

    tpe match {
      case "major" => Key(root, Scale.MAJOR)
      case "minor" => Key(root, Scale.MINOR)
      case _ => readKey
    }
  }

  def readNotes: List[Note] = {
    StdIOTools.read[Array[Int]]("Input midi note numbers separated by spaces", "Invalid input", {
      scala.io.StdIn.readLine.split(" ").map(_.toInt)
    }).map { i =>
      val noteNumber = MidiNoteNumber(i)
      Note(noteNumber.toOct, noteNumber.toPc)
    }.toList
  }

}
