package client.io.terminal

import client.MusicReader
import client.MusicReader.{NoteOn, Simultaneous}
import client.io.StdIOTools
import javax.inject.Singleton
import midi.MidiIO
import music.primitives._

@Singleton
class TerminalMusicReader extends MusicReader {

  import music.analysis.TwelveToneTuning._

  override def readPitchSpelling: MidiIO[PitchSpelling] = {
    val step = Step(StdIOTools.readInt("Input step"))
    val acc = Accidental(StdIOTools.readInt("Input accidental"))
    Right(PitchSpelling(step, acc))
  }

  override def readTimeSignatureDivision: MidiIO[TimeSignatureDivision] = {
    val division = StdIOTools.read[(Int, Int)]("Input time signature: x/y", "Invalid value", {
      val elems = scala.io.StdIn.readLine.split("/")
      (elems(0).toInt, elems(1).toInt)
    })

    TimeSignatureDivision(division._1, division._2) match {
      case Some(ts) => Right(ts)
      case None =>
        println("Invalid time signature")
        readTimeSignatureDivision
    }
  }

  override def readPitchClasses(method: MusicReader.ReadMethod): MidiIO[List[PitchClass]] = {
    method match {
      case Simultaneous =>
        val res = StdIOTools.read[Array[Int]]("Input pitch classes separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        }).map(PitchClass.apply).toList
        Right(res)

      case NoteOn(n) =>
        val list = StdIOTools.read[Array[Int]](s"Input [$n] pitch classes separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        })
        if (list.length != n) {
          println(s"Expected [$n] values but got [{${list.length}]")
          readPitchClasses(method)
        } else {
          Right(list.map(PitchClass.apply).toList)
        }
    }
  }

  override def readMidiNoteNumbers(method: MusicReader.ReadMethod): MidiIO[List[MidiNoteNumber]] = {
    method match {
      case Simultaneous =>
        val res = StdIOTools.read[Array[Int]]("Input midi note numbers separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        }).map(i => MidiNoteNumber(i)).toList
        Right(res)

      case NoteOn(n) =>
        val list = StdIOTools.read[Array[Int]](s"Input [$n] midi note numbers separated by spaces", "Invalid input", {
          scala.io.StdIn.readLine.split(" ").map(_.toInt)
        })
        if (list.length != n) {
          println(s"Expected [$n] values but got [{${list.length}]")
          readMidiNoteNumbers(method)
        } else {
          Right(list.map(i => MidiNoteNumber(i)).toList)
        }
    }
  }

}
