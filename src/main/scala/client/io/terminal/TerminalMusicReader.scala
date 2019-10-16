package client.io.terminal

import client.MusicReader
import client.MusicReader.{NoteOn, Simultaneous}
import client.io.StdIOTools
import music.analysis.TwelveToneEqualTemprament
import music.primitives.{Accidental, MidiNoteNumber, PitchClass, TimeSignatureDivision}
import music.spelling.SpelledPitch

class TerminalMusicReader extends MusicReader {

  override def readSpelledPitch: SpelledPitch = {
    val step = TwelveToneEqualTemprament.tuning.step(StdIOTools.readInt("Input step"))
    val acc = Accidental(StdIOTools.readInt("Input accidental"))
    SpelledPitch(step, acc)
  }

  override def readTimeSignatureDivision: TimeSignatureDivision = {
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

  override def readPitchClasses(method: MusicReader.ReadMethod): List[PitchClass] = {
    method match {
      case Simultaneous =>
        StdIOTools.read[Array[Int]]("Input pitch classes separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        }).map(TwelveToneEqualTemprament.tuning.pc).toList

      case NoteOn(n) =>
        val list = StdIOTools.read[Array[Int]](s"Input [$n] pitch classes separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        })
        if (list.length != n) {
          println(s"Expected [$n] values but got [{${list.length}]")
          readPitchClasses(method)
        } else {
          list.map(TwelveToneEqualTemprament.tuning.pc).toList
        }
    }
  }

  override def readMidiNoteNumbers(method: MusicReader.ReadMethod): List[MidiNoteNumber] = {
    method match {
      case Simultaneous =>
        StdIOTools.read[Array[Int]]("Input midi note numbers separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        }).map(MidiNoteNumber).toList

      case NoteOn(n) =>
        val list = StdIOTools.read[Array[Int]](s"Input [$n] midi note numbers separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        })
        if (list.length != n) {
          println(s"Expected [$n] values but got [{${list.length}]")
          readMidiNoteNumbers(method)
        } else {
          list.map(MidiNoteNumber).toList
        }
    }
  }

}
