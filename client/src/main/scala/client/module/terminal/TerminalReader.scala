package client.module.terminal

import client.module.StdIOTools
import domain.primitives._
import domain.math._

import scala.annotation.tailrec

object TerminalReader {

  import domain.write.analysis.TwelveToneTuning._

  def readMetre: Metre = {
    val (num, denom) = StdIOTools.read[(Int, Int)]("Input time signature: x/y", "Invalid value", {
      val elems = scala.io.StdIn.readLine.split("/")
      (elems(0).toInt, elems(1).toInt)
    })

    if (! denom.isPowerOfTwo) {
      println(s"Invalid time signature denominator $denom")
      readMetre
    } else {
      val baseDuration = denom.largestPowerOfTwo
      val pulseGroups = num match {
        case num if num == 1 =>
          Seq(PulseGroup(baseDuration, 1))

        case num if num % 2 == 0 =>
          Seq.tabulate(num / 2)(_ => PulseGroup(baseDuration, 2))

        case num if num % 3 == 0 =>
          Seq.tabulate(num / 3)(_ => PulseGroup(baseDuration, 3))

        case num =>
          PulseGroup(baseDuration, 3) +: Seq.tabulate((num - 3) / 2)(_ => PulseGroup(baseDuration, 2))

      }
      Metre(pulseGroups)
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
