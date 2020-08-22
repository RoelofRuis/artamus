package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.analysis.{ChordAnalysis, RNA}
import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Degree, Key, PitchDescriptor}

import scala.io.StdIn

object Degrees extends App {

  import Read._
  import Write._

  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get

  if (StdIn.readLine("D: Degrees, else Chords\n > ") == "D") {
    val chordAnalysis = ChordAnalysis(tuning)
    val degreeInput: Array[Degree] = tuning.parseArray(tuning.parseDegree).run(StdIn.readLine("Input degrees separated by a space\n > "))._2
    val root: PitchDescriptor = tuning.parsePitchDescriptor.run(StdIn.readLine("Input root\n > "))._2
    val chords = chordAnalysis.nameChords(degreeInput, root)
    print(tuning.printChords(chords))
  } else {
    val rna = RNA(tuning)
    val chordInput: Array[Chord] = tuning.parseArray(tuning.parseChord).run(StdIn.readLine("Input chords separated by a space\n > "))._2
    val key: Key = tuning.parseKey.run(StdIn.readLine("Input key\n > "))._2
    val degrees = rna.nameDegrees(chordInput, key)
    print(tuning.printDegrees(degrees))
  }

}