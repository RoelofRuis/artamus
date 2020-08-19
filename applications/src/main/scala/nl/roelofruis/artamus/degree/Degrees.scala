package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, PitchDescriptor}

import scala.io.StdIn

object Degrees extends App {

  import Harmony._
  import Read._
  import Write._

  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get

  if (StdIn.readLine("D: Degrees, else Chords\n > ") == "D") {
    val degreeInput: Array[Degree] = tuning.parseArray(StdIn.readLine("Input degrees separated by a space\n > "), tuning.parseDegree)
    val root: PitchDescriptor = tuning.parsePitchDescriptor(StdIn.readLine("Input root\n > "))
    val chords = tuning.nameChords(degreeInput, root)
    print(tuning.printChords(chords))
  } else {
    val chordInput: Array[Chord] = tuning.parseArray(StdIn.readLine("Input chords separated by a space\n > "), tuning.parseChord)
    val root: PitchDescriptor = tuning.parsePitchDescriptor(StdIn.readLine("Input key\n > "))
    val degrees = tuning.nameDegrees(chordInput, root)
    print(tuning.printDegrees(degrees))
  }

}