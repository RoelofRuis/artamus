package nl.roelofruis.artamus.degree

import nl.roelofruis.artamus.analysis.RNA
import nl.roelofruis.artamus.degree.FileModel.TextTuning
import nl.roelofruis.artamus.degree.Model.{Chord, Key}

import scala.io.StdIn

object Degrees extends App {

  import Read._
  import Write._

  val tuning = FileModel.load[TextTuning]("applications/res/tuning.json").get
  val chords = FileModel.read("applications/res/all_the_things_you_are.txt")

  val rna = RNA(tuning)
  val chordInput: Array[Chord] = tuning.parseArray(tuning.parseChord).run(chords)._2
  println(chords)
  val key: Key = tuning.parseKey.run(StdIn.readLine("Input key\n > "))._2
  val degrees = rna.nameDegrees(chordInput, key)

  print(tuning.printDegrees(degrees))

}